package com.backend.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {
    private String id;
    private String email;
    private String name;
    private String picture;
    private String googleId;
    private boolean isNewUser; // Cambiado para que sea más claro
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    
    // Constructor para usuarios nuevos
    public AuthUser(String email, String googleId) {
        this.email = email;
        this.googleId = googleId;
        this.isNewUser = true;
        this.createdAt = LocalDateTime.now();
        this.lastLoginAt = LocalDateTime.now();
    }
} 