package com.an.chat.dto.response;

import java.time.Instant;
import java.util.List;

import com.an.chat.entity.ParticipantInfo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String id;
    String type; // GROUP, DIRECT
    String participantsHash;
    String conversationAvatar; // 2 field này mới vì khi ở trong tài khoản người này thì sẽ hiện avatar của người kia.
    String conversationName; // -> đây là file nội suy từ participants
    List<ParticipantInfo> participants;
    Instant createdDate;
    Instant modifiedDate;
}
