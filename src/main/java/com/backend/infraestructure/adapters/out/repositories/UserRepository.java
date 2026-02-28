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

    List<UserEntity> findAllByOrderByNameAsc();
    
    Optional<UserEntity> findByEmail(String email);
    
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    
    // Consultas para múltiples roles
    @Query("{'roles': ?0}")
    List<UserEntity> findByRole(UserModel.Role role);
    
    @Query("{'roles': 'MUSICIAN'}")
    List<UserEntity> findAllMusicians();
    
    @Query("{'roles': 'DIRECTOR'}")
    List<UserEntity> findAllDirectors();
    
    @Query("{'roles': 'ADMIN'}")
    List<UserEntity> findAllAdmins();
    
    // Consulta para usuarios que tienen al menos uno de los roles especificados
    @Query("{'roles': {'$in': ?0}}")
    List<UserEntity> findByRolesIn(List<UserModel.Role> roles);
} 