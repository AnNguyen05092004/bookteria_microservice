package com.example.notification_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

// response brevo trả về
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailResponse {
    String messageId;
}
