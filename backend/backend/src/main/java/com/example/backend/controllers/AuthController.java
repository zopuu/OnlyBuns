package com.example.backend.controllers;

import com.example.backend.dto.UserRegistrationDto;
import com.example.backend.services.AuthService;
import com.example.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    //private final AuthenticationManager authenticationManager;
    private final AuthService authService;

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
}
