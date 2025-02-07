package com.ex.library.controller;

import com.ex.library.dto.request.SendEmailRequest;
import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.response.EmailResponse;
import com.ex.library.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class SendMailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public APIResponse<EmailResponse> sendMail(@RequestBody SendEmailRequest emailRequest) throws MessagingException {
        return APIResponse.<EmailResponse>builder()
                .response(emailService.sendEmail(emailRequest,"welcomeEmail","",""))
                .build();
    }
}
