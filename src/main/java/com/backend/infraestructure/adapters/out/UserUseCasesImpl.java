package com.backend.infraestructure.adapters.out;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.stereotype.Repository;

import com.backend.domain.model.UserModel;
import com.backend.domain.port.UserUseCases;
import com.backend.infraestructure.adapters.out.entities.UserEntity;
import com.backend.infraestructure.adapters.out.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserUseCasesImpl implements UserUseCases {
    
    private final UserRepository userRepository;

    
    @Override
    public UserModel createUser(UserModel user) {
        UserEntity userEntity = UserEntity.fromDomain(user);
        UserEntity savedEntity = userRepository.save(userEntity);
        return savedEntity.toDomain();
    }
    
    @Override
    public UserModel getUserByEmail(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        return userEntity.map(UserEntity::toDomain).orElse(null);
    }
    
    @Override
    public UserModel getUserById(String id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        return userEntity.map(UserEntity::toDomain).orElse(null);
    }
    
    @Override
    public UserModel updateUser(UserModel user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("ID de usuario es requerido para actualizar");
        }
        
        // Verificar que el usuario existe
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + user.getId());
        }
        
        UserEntity userEntity = UserEntity.fromDomain(user);
        UserEntity savedEntity = userRepository.save(userEntity);
        return savedEntity.toDomain();
    }
    
    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public List<UserModel> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream()
                .map(UserEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserModel getUserByPhoneNumber(String phoneNumber) {
        Optional<UserEntity> userEntity = userRepository.findByPhoneNumber(phoneNumber);
        return userEntity.map(UserEntity::toDomain).orElse(null);
    }
}
