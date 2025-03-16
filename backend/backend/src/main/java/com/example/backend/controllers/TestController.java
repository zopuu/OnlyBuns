package com.example.backend.controllers;

import com.example.backend.utils.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final EmailService emailService;

    public TestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    public ResponseEntity<String> sendTestMail() {
        emailService.sendVerificationEmail(
                "veka.zopu@gmail.com", // your real test email
                "dummy-token"
        );
        return ResponseEntity.ok("Mail sent");
    }
}
