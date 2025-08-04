package com.backend.infraestructure.adapters.out.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.backend.domain.model.UserModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    
    @Id
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserModel.Role role;
    private LocalDateTime createdAt;
    
    public static UserEntity fromDomain(UserModel userModel) {
        UserEntity entity = new UserEntity();
        entity.setId(userModel.getId());
        entity.setName(userModel.getName());
        entity.setEmail(userModel.getEmail());
        entity.setPhoneNumber(userModel.getPhoneNumber());
        entity.setRole(userModel.getRole());
        entity.setCreatedAt(userModel.getCreatedAt());
        return entity;
    }
    
    public UserModel toDomain() {
        UserModel userModel = new UserModel();
        userModel.setId(this.id);
        userModel.setName(this.name);
        userModel.setEmail(this.email);
        userModel.setPhoneNumber(this.phoneNumber);
        userModel.setRole(this.role);
        userModel.setCreatedAt(this.createdAt);
        return userModel;
    }
} 