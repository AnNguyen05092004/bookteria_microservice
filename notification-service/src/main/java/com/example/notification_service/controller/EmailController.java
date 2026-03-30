package com.example.notification_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.notification_service.dto.ApiResponse;
import com.example.notification_service.dto.request.SendEmailRequest;
import com.example.notification_service.dto.response.EmailResponse;
import com.example.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;

    @PostMapping("/email/send")
    public ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest sendEmailRequest) {
        return ApiResponse.<EmailResponse>builder()
                .code(1000)
                .message("Email sent successfully")
                .result(emailService.sendEmail(sendEmailRequest))
                .build();
    }
}
