package com.example.backend.services;

import com.example.backend.dto.UserRegistrationDto;
import com.example.backend.models.Role;
import com.example.backend.models.User;
import com.example.backend.models.VerificationToken;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.VerificationTokenRepository;
import com.example.backend.utils.EmailService;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.MessageUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
    public void createUser(UserRegistrationDto userDto) {
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

    }
    public void verifyUserAccount(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }
    public User updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setAddress(updatedUser.getAddress());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

}
