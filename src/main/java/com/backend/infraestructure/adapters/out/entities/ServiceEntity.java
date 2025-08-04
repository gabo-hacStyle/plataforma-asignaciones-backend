package com.backend.infraestructure.adapters.out.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.backend.domain.model.ServiceModel;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "services")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEntity {
    
    @Id
    private String id;
    private LocalDate serviceDate;
    private LocalDate practiceDate;
    private List<UserEntity> directors;
    private List<MusiciansListEntity> musiciansList;
    private List<SongsEntity> songsList;
    private String location;
    private LocalDateTime createdAt;
    
    public static ServiceEntity fromDomain(ServiceModel serviceModel) {
        ServiceEntity entity = new ServiceEntity();
        entity.setId(serviceModel.getId());
        entity.setServiceDate(serviceModel.getServiceDate());
        entity.setPracticeDate(serviceModel.getPracticeDate());
        entity.setLocation(serviceModel.getLocation());
        entity.setCreatedAt(serviceModel.getCreatedAt());
        
        // Convertir directores
        if (serviceModel.getDirectors() != null) {
            entity.setDirectors(serviceModel.getDirectors().stream()
                    .map(UserEntity::fromDomain)
                    .collect(Collectors.toList()));
        }
        
        // Convertir lista de músicos
        if (serviceModel.getMusiciansList() != null) {
            entity.setMusiciansList(serviceModel.getMusiciansList().stream()
                    .map(MusiciansListEntity::fromDomain)
                    .collect(Collectors.toList()));
        }
        
        // Convertir lista de canciones
        if (serviceModel.getSongsList() != null) {
            entity.setSongsList(serviceModel.getSongsList().stream()
                    .map(SongsEntity::fromDomain)
                    .collect(Collectors.toList()));
        }
        
        return entity;
    }
    
    public ServiceModel toDomain() {
        ServiceModel serviceModel = new ServiceModel();
        serviceModel.setId(this.id);
        serviceModel.setServiceDate(this.serviceDate);
        serviceModel.setPracticeDate(this.practiceDate);
        serviceModel.setLocation(this.location);
        serviceModel.setCreatedAt(this.createdAt);
        
        // Convertir directores
        if (this.directors != null) {
            serviceModel.setDirectors(this.directors.stream()
                    .map(UserEntity::toDomain)
                    .collect(Collectors.toList()));
        }
        
        // Convertir lista de músicos
        if (this.musiciansList != null) {
            serviceModel.setMusiciansList(this.musiciansList.stream()
                    .map(MusiciansListEntity::toDomain)
                    .collect(Collectors.toList()));
        }
        
        // Convertir lista de canciones
        if (this.songsList != null) {
            serviceModel.setSongsList(this.songsList.stream()
                    .map(SongsEntity::toDomain)
                    .collect(Collectors.toList()));
        }
        
        return serviceModel;
    }
} 