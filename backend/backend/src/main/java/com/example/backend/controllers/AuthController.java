package com.example.backend.controllers;

import com.example.backend.dto.JwtResponse;
import com.example.backend.dto.LoginDto;
import com.example.backend.dto.UserRegistrationDto;
import com.example.backend.services.AuthService;
import com.example.backend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

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
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        try {
            authService.login(loginDto, response);
            return ResponseEntity.ok("Login successful!");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials!");
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        jwtUtil.clearJwtCookie(response);
        return ResponseEntity.ok("Logout successful!");
    }
}
