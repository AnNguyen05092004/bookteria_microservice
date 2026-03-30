package com.example.notification_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

// Request này là từ User
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailRequest {
    String htmlContent;
    String subject;
    Recipient to;
}
