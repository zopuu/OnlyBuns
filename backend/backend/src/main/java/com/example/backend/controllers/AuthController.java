package com.example.backend.controllers;

import com.example.backend.dto.LoginDto;
import com.example.backend.dto.UserRegistrationDto;
import com.example.backend.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto userDto){
        authService.createUser(userDto);
        return ResponseEntity.ok("Registration successful! Please check your email for verification.");
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam String token){
        authService.verifyUserAccount(token);
        return ResponseEntity.ok("Account verification successful! You can now login.");
    }
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        try {
            authService.login(loginDto, response);
            return ResponseEntity.ok("Login successful!");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials!");
        }
    }
}
