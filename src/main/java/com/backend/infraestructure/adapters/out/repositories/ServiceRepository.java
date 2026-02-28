package com.backend.infraestructure.adapters.out.repositories;

import java.time.LocalDate;
import java.util.List;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.infraestructure.adapters.out.entities.ServiceEntity;

@Repository
public interface ServiceRepository extends MongoRepository<ServiceEntity, String> {

    List<ServiceEntity> findAllByOrderByServiceDateAsc();

    @Query("{'serviceDate': ?0}")
    List<ServiceEntity> findByServiceDate(LocalDate serviceDate);

    @Query(value = "{'directors.id': ?0}", sort = "{'serviceDate': 1}")
    List<ServiceEntity> findByDirectorId(String directorId);
    
    @Query(value = "{'musiciansList.musician.id': ?0}", sort = "{'serviceDate': 1}")
    List<ServiceEntity> findByMusicianId(String musicianId);
    

} 