package com.example.notification_service.dto.request;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

// Request này là cho brevo
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {
    String htmlContent;
    Sender sender;
    String subject;
    List<Recipient> to;
}
