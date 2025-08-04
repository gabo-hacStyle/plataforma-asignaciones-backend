package com.backend.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private LocalDateTime createdAt;

    public static enum Role {
        ADMIN,
        DIRECTOR,
        MUSICIAN
    }
}
