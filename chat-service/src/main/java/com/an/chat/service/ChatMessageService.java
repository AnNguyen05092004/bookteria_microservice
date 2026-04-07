package com.an.chat.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.an.chat.entity.WebSocketSession;
import com.an.chat.repository.WebSocketSessionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.an.chat.dto.request.ChatMessageRequest;
import com.an.chat.dto.response.ChatMessageResponse;
import com.an.chat.entity.ChatMessage;
import com.an.chat.entity.ParticipantInfo;
import com.an.chat.exception.AppException;
import com.an.chat.exception.ErrorCode;
import com.an.chat.mapper.ChatMessageMapper;
import com.an.chat.repository.ChatMessageRepository;
import com.an.chat.repository.ConversationRepository;
import com.an.chat.repository.httpclient.ProfileClient;
import com.corundumstudio.socketio.SocketIOServer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {
    ChatMessageRepository chatMessageRepository;
    ConversationRepository conversationRepository;
    ProfileClient profileClient;
    ChatMessageMapper chatMessageMapper;
    SocketIOServer socketIOServer;
    WebSocketSessionRepository webSocketSessionRepository;
    ObjectMapper objectMapper; // để chuyển từ object sang json

    public List<ChatMessageResponse> getMessages(String conversationId) {
        // validate conversation
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // validate conversation exists
        var consersation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
        // validate if user in that conversation
        consersation.getParticipants().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_CONVERSATION));

        // get message
        var messages = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId);

        return messages.stream().map(this::toChatMessageResponse).toList();
    }

    public ChatMessageResponse create(ChatMessageRequest request) throws JsonProcessingException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // validate conversation exists
        var consersation = conversationRepository
                .findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        // validate if user in that conversation
        consersation.getParticipants().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_CONVERSATION));

        // get userInfo from profile service
        var userResponse = profileClient.getProfile(userId);
        if (Objects.isNull(userResponse)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        var userInfo = userResponse.getResult();

        // build chat message info
        ChatMessage chatMessage = chatMessageMapper.toChatMessage(request);
        chatMessage.setSender(ParticipantInfo.builder()
                .userId(userInfo.getUserId())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .avatar(userInfo.getAvatar())
                .build());
        chatMessage.setCreatedDate(Instant.now());

        // create chat message
        chatMessage = chatMessageRepository.save(chatMessage); // gán ngược lại để có thêm id

        // publish socket event to client in that conversation
        // Get participants userId
        List<String> userIds = consersation.getParticipants().stream().map(ParticipantInfo::getUserId).toList();
       // String message = objectMapper.writeValueAsString(chatMessage); // chuyển chatMessage sang json để gửi qua socket

        // Lấy ra các socketssesion trong conversation đó
        Map<String, WebSocketSession> webSocketSessions =
                webSocketSessionRepository
                        .findAllByUserIdIn(userIds).stream()
                        .collect(Collectors.toMap(
                                WebSocketSession::getSocketSessionId,
                                Function.identity()));

        ChatMessageResponse chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);
        socketIOServer.getAllClients().forEach(client -> {
            var webSocketSession = webSocketSessions.get(client.getSessionId().toString());

            if (Objects.nonNull(webSocketSession)) {
                String message = null;
                try {
                    chatMessageResponse.setMe(webSocketSession.getUserId().equals(userId));
                    message = objectMapper.writeValueAsString(chatMessageResponse);
                    client.sendEvent("message", message);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // convert to response
        return toChatMessageResponse(chatMessage);
    }

    // Để set thêm boolean me
    private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        var chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        chatMessageResponse.setMe(userId.equals(chatMessage.getSender().getUserId()));
        return chatMessageResponse;
    }
}
