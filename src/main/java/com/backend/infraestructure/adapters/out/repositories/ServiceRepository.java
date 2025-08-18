package com.backend.infraestructure.adapters.out.repositories;

import java.time.LocalDate;
import java.util.List;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.infraestructure.adapters.out.entities.ServiceEntity;

@Repository
public interface ServiceRepository extends MongoRepository<ServiceEntity, String> {

    // No es necesario sobrescribir el método findAll() de MongoRepository a menos que quieras un orden específico.
    // Si solo quieres obtener todos los servicios, puedes usar el findAll() heredado sin anotación @Query.
    // Si necesitas que siempre estén ordenados por 'serviceDate', es mejor definir un método específico:

    

    // O simplemente usar el método de Spring Data:
    List<ServiceEntity> findAllByOrderByServiceDateAsc();

    // El método findAll() original de MongoRepository ya existe y no necesita redefinirse.
    
    @Query("{'serviceDate': ?0}")
    List<ServiceEntity> findByServiceDate(LocalDate serviceDate);
    
    @Query("{'serviceDate': {$gte: ?0, $lte: ?1}}")
    List<ServiceEntity> findByServiceDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query(value = "{'directors.id': ?0}", sort = "{'serviceDate': 1}")
    List<ServiceEntity> findByDirectorId(String directorId);
    
    @Query(value = "{'musiciansList.musician.id': ?0}", sort = "{'serviceDate': 1}")
    List<ServiceEntity> findByMusicianId(String musicianId);
    
    @Query("{'serviceDate': {$lt: ?0}}")
    List<ServiceEntity> findExpiredServices(LocalDate currentDate);
} 