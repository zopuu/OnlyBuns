package com.example.backend.services;

import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false); // Requires email verification
        return userRepository.save(user);
    }
    public User updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
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
