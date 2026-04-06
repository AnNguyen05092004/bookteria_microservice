package com.an.chat.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    @MongoId
    String id;

    String type; // GROUP, DIRECT

    @Indexed(unique = true)
    String participantsHash; // đảm bảo chỉ có 1 conversation giữa 2 người

    List<ParticipantInfo>
            participants; // embeded vào để đọc nhanh hơn, nhưng cần cập nhật khi có thay đổi thông tin người dùng

    Instant createdDate;

    Instant modifiedDate;
}
