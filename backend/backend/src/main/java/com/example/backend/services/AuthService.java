package com.example.backend.services;

import com.example.backend.dto.LoginDto;
import com.example.backend.dto.UserRegistrationDto;
import com.example.backend.models.Role;
import com.example.backend.models.User;
import com.example.backend.models.VerificationToken;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.VerificationTokenRepository;
import com.example.backend.utils.EmailService;
import com.example.backend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void createUser(UserRegistrationDto userDto) {
        try{
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new DataIntegrityViolationException("Email already exists");
            }
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new DataIntegrityViolationException("Username already exists");
            }
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setAddress(userDto.getAddress());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setRole(Role.USER);
            user.setEnabled(false); // Requires email verification
            userRepository.save(user);

            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));

            verificationTokenRepository.save(verificationToken);


            emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());
        } catch (DataIntegrityViolationException e){
            throw new RuntimeException("Registration failed", e);
        }
    }
    public void verifyUserAccount(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }
    public void login(LoginDto loginDto, HttpServletResponse response){
        logger.debug("Entering login in AuthService");

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        logger.debug("Authentication token: {}", authToken);
        Authentication authentication = authenticationManager.authenticate(authToken);
        logger.debug("Authenticated user: {}", authentication.getPrincipal());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        logger.debug("User details: {}", userDetails);
        jwtUtil.generateTokenAndSetCookie(userDetails, response);
        logger.debug("response: {}", response);
    }
}
