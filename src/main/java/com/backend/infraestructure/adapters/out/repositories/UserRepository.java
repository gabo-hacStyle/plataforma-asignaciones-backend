package com.backend.infraestructure.adapters.out.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.domain.model.UserModel;
import com.backend.infraestructure.adapters.out.entities.UserEntity;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {
    
    Optional<UserEntity> findByEmail(String email);
    
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    
    @Query("{'role': ?0}")
    List<UserEntity> findByRole(UserModel.Role role);
    
    @Query("{'role': 'MUSICIAN'}")
    List<UserEntity> findAllMusicians();
    
    @Query("{'role': 'DIRECTOR'}")
    List<UserEntity> findAllDirectors();
} 