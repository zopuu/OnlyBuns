package com.example.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendVerificationEmail(String to, String token) {
        logger.info("Sending verification email to {}", to);
        String verificationUrl = "http://localhost:8081/api/users/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("chocolatefactory087@gmail.com");
        message.setTo(to);
        message.setSubject("Activate Your Account");
        message.setText("Click the ling to activate your account: " + verificationUrl);
        mailSender.send(message);
        logger.info("Email successfully sent to {}", to);
    }
}
