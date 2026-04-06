package com.an.chat.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.an.chat.dto.request.ConversationRequest;
import com.an.chat.dto.response.ConversationResponse;
import com.an.chat.entity.Conversation;
import com.an.chat.entity.ParticipantInfo;
import com.an.chat.exception.AppException;
import com.an.chat.exception.ErrorCode;
import com.an.chat.mapper.ConversationMapper;
import com.an.chat.repository.ConversationRepository;
import com.an.chat.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    ProfileClient profileClient;

    ConversationMapper conversationMapper;

    public List<ConversationResponse> myConversations() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var conversations = conversationRepository.findAllByParticipantIdsContains(userId);

        return conversations.stream().map(this::toConversationResponse).toList();
    }

    public ConversationResponse create(ConversationRequest request) {
        // fetch user info
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var userInfoResponse = profileClient.getProfile(userId);
        var participantInfoResponse = profileClient.getProfile(
                request.getParticipantIds().getFirst()); // Hiện chỉ làm chat 2 người nên lấy 1 parti thoi

        if (Objects.isNull(userInfoResponse) || Objects.isNull(participantInfoResponse)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        var userInfor = userInfoResponse.getResult();
        var participantInfor = participantInfoResponse.getResult();

        // Tạo hashId
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(participantInfor.getUserId());

        var sortedIds = userIds.stream().sorted().toList();
        String userIdHase = generateParticipantHash(sortedIds);

        // kiểm tra xem chat đã tồn tại chưa, nếu đã tồn tại thì trả về luôn, không tạo mới
        var conversation = conversationRepository
                .findByParticipantsHash(userIdHase)
                .orElseGet(() -> {
                    // Tạo mới conversation
                    // Tạo list participant info tham gia vào đoạn chat
                    List<ParticipantInfo> participantInfos = List.of(
                            ParticipantInfo.builder()
                                    .userId(userInfor.getUserId())
                                    .username(userInfor.getUsername())
                                    .firstName(userInfor.getFirstName())
                                    .lastName(userInfor.getLastName())
                                    .avatar(userInfor.getAvatar())
                                    .build(),
                            ParticipantInfo.builder()
                                    .userId(participantInfor.getUserId())
                                    .username(participantInfor.getUsername())
                                    .firstName(participantInfor.getFirstName())
                                    .lastName(participantInfor.getLastName())
                                    .avatar(participantInfor.getAvatar())
                                    .build());

                    // Build conversation
                    Conversation newConversation = Conversation.builder()
                            .type(request.getType())
                            .participantsHash(userIdHase)
                            .createdDate(Instant.now())
                            .modifiedDate(Instant.now())
                            .participants(participantInfos)
                            .build();
                    return conversationRepository.save(newConversation);
                });

        return toConversationResponse(conversation);
    }

    // Generate a unique hash for the conversation based on participant IDs
    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        // hoặc có thể dùng SHA256 để hash cũng được
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    // Mapper + trả về name+info của participant đối diện để hiển thị ở client
    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId =
                SecurityContextHolder.getContext().getAuthentication().getName();

        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
                .findFirst()
                .ifPresent(participantInfo -> {
                    String name = participantInfo.getUsername();
                    if (name == null || name.isBlank()) {
                        name = ((participantInfo.getFirstName() == null ? "" : participantInfo.getFirstName())
                                        + " "
                                        + (participantInfo.getLastName() == null ? "" : participantInfo.getLastName()))
                                .trim();
                    }
                    conversationResponse.setConversationName(name);
                    conversationResponse.setConversationAvatar(participantInfo.getAvatar());
                });
        return conversationResponse;
    }
}
