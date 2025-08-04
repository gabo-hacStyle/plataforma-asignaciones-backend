package com.backend.infraestructure.adapters.out.repositories;

import java.time.LocalDate;
import java.util.List;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.infraestructure.adapters.out.entities.ServiceEntity;

@Repository
public interface ServiceRepository extends MongoRepository<ServiceEntity, String> {
    
    @Query("{'serviceDate': ?0}")
    List<ServiceEntity> findByServiceDate(LocalDate serviceDate);
    
    @Query("{'serviceDate': {$gte: ?0, $lte: ?1}}")
    List<ServiceEntity> findByServiceDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("{'directors.id': ?0}")
    List<ServiceEntity> findByDirectorId(String directorId);
    
    @Query("{'musiciansList.musician.id': ?0}")
    List<ServiceEntity> findByMusicianId(String musicianId);
    
    @Query("{'serviceDate': {$lt: ?0}}")
    List<ServiceEntity> findExpiredServices(LocalDate currentDate);
} 