package com.an.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class NotificationEvent {
    String channel;
    String recipient;
    String templateCode;
    Map<String, Object> param; // dùng để lưu thông tin đục lỗ cho template
    String subject;
    String body;
}
