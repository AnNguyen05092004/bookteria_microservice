package com.an.notification_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.an.notification_service.dto.request.EmailRequest;
import com.an.notification_service.dto.request.SendEmailRequest;
import com.an.notification_service.dto.request.Sender;
import com.an.notification_service.dto.response.EmailResponse;
import com.an.notification_service.exception.AppException;
import com.an.notification_service.exception.ErrorCode;
import com.an.notification_service.repository.httpclient.EmailClient;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailService {
    final EmailClient emailClient;
    final String apiKey = "";
    String senderEmail = "an05092004@gmail.com";
    String senderName = "an";

    public EmailResponse sendEmail(SendEmailRequest request) {

        EmailRequest emailRequest = EmailRequest.builder()
                .htmlContent(request.getHtmlContent())
                .sender(Sender.builder().email(senderEmail).name(senderName).build())
                .subject(request.getSubject())
                .to(List.of(request.getTo()))
                .build();

        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e) {
            log.error("Brevo send email failed. status={}, body={}", e.status(), e.contentUTF8());
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        } catch (Exception e) {
            log.error("Email request encode/send failed", e);
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
