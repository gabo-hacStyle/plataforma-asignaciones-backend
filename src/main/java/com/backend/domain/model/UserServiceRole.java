package com.backend.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceRole {
    private String userId;
    private String serviceId;
    private UserModel.Role role;
    private LocalDateTime assignedAt;
    
    public static enum Role {
        ADMIN,
        DIRECTOR,
        MUSICIAN
    }
} 