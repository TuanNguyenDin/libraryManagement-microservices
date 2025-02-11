package com.ex.library.service;

import com.ex.library.dto.request.EmailInforRequest;
import com.ex.library.dto.request.EmailRequest;
import com.ex.library.dto.request.SendEmailRequest;
import com.ex.library.dto.response.EmailResponse;
import com.ex.library.exception.CustomException;
import com.ex.library.exception.ErrorCode;
import com.ex.library.repository.EmailClient;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    private EmailClient emailClient;
    private final TemplateEngine templateEngine;

    @NonFinal
    @Value("${spring.mail.brevo.apikey}")
    String brevoApiKey;

    @NonFinal
    EmailInforRequest sender = EmailInforRequest.builder()
            .name("TuanSPM")
            .email("dinhtuann987161@gmail.com")
            .build();

    public EmailResponse sendEmail(SendEmailRequest emailRequest, String mailTemplate, String verifyCode, String verifyUrl) throws MessagingException {


        EmailRequest email = EmailRequest.builder()
                .sender(sender)
                .to(List.of(emailRequest.getTo()))
                .subject(emailRequest.getSubject())
                .htmlContent(createContextSend(emailRequest, mailTemplate, verifyUrl, verifyCode))
                .build();

        try {
            log.info("Create email success");
            return emailClient.sendEmail(brevoApiKey, email);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(ErrorCode.SEND_MAIL_ERROR);
        }
    }

    private String createContextSend(SendEmailRequest emailRequest, String mailTemplate, String verifyCode, String url) {
        Context context = new Context();
        context.setVariable("serviceName", "Library Service");
        context.setVariable("userName", emailRequest.getTo().getName());
        context.setVariable("username", emailRequest.getTo().getName());
        context.setVariable("registrationDate", new java.sql.Date(System.currentTimeMillis()));
        context.setVariable("Url", url);
        context.setVariable("newEmail", emailRequest.getTo().getEmail());
        context.setVariable("verifyCode", verifyCode);
        context.setVariable("supportEmail", "<EMAIL>");

        String htmlContent = templateEngine.process(mailTemplate, context);
        return htmlContent;
    }
}
