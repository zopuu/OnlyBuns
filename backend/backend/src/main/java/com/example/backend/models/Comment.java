package com.example.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500,nullable = false)
    private String text;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
