package com.backend.domain.model;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Role> roles; // Cambiado de Role role a List<Role> roles
    private LocalDateTime createdAt;

    public static enum Role {
        ADMIN,
        DIRECTOR,
        MUSICIAN
    }
}
