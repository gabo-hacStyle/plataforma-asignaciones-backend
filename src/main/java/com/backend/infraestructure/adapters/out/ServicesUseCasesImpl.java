package com.backend.infraestructure.adapters.out;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.stereotype.Repository;

import com.backend.domain.model.ServiceModel;
import com.backend.domain.port.ServicesUseCases;
import com.backend.infraestructure.adapters.out.entities.ServiceEntity;
import com.backend.infraestructure.adapters.out.repositories.ServiceRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ServicesUseCasesImpl implements ServicesUseCases {
    
    private final ServiceRepository serviceRepository;
    
    
    @Override
    public ServiceModel createService(ServiceModel service) {
        ServiceEntity serviceEntity = ServiceEntity.fromDomain(service);
        ServiceEntity savedEntity = serviceRepository.save(serviceEntity);
        return savedEntity.toDomain();
    }
    
    @Override
    public ServiceModel getServiceById(String id) {
        Optional<ServiceEntity> serviceEntity = serviceRepository.findById(id);
        return serviceEntity.map(ServiceEntity::toDomain).orElse(null);
    }
    
    @Override
    public ServiceModel updateService(ServiceModel service) {
        if (service.getId() == null) {
            throw new IllegalArgumentException("ID de servicio es requerido para actualizar");
        }
        
        // Verificar que el servicio existe
        if (!serviceRepository.existsById(service.getId())) {
            throw new IllegalArgumentException("Servicio no encontrado con ID: " + service.getId());
        }
        
        ServiceEntity serviceEntity = ServiceEntity.fromDomain(service);
        ServiceEntity savedEntity = serviceRepository.save(serviceEntity);
        return savedEntity.toDomain();
    }
    
    @Override
    public void deleteService(String id) {
        if (!serviceRepository.existsById(id)) {
            throw new IllegalArgumentException("Servicio no encontrado con ID: " + id);
        }
        serviceRepository.deleteById(id);
    }
    
    @Override
    public List<ServiceModel> getAllServices() {
        List<ServiceEntity> serviceEntities = serviceRepository.findAllByOrderByServiceDateAsc();
        return serviceEntities.stream()
                .map(ServiceEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ServiceModel> getServicesByDirector(String directorId) {
        List<ServiceEntity> serviceEntities = serviceRepository.findByDirectorId(directorId);
        return serviceEntities.stream()
                .map(ServiceEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ServiceModel> getServicesByMusician(String musicianId) {
        List<ServiceEntity> serviceEntities = serviceRepository.findByMusicianId(musicianId);
        return serviceEntities.stream()
                .map(ServiceEntity::toDomain)
                .collect(Collectors.toList());
    }
} 