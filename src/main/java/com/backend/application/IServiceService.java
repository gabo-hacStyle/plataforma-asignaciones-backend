package com.backend.application;

import java.time.LocalDate;
import java.util.List;

import com.backend.application.dto.CreateSongListRequest;
import com.backend.application.dto.MusicianAssignment;
import com.backend.application.dto.UpdateAssingmentRequest;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;

public interface IServiceService {
    
    // Historias de usuario del Admin
    ServiceModel createService(ServiceModel service);
    ServiceModel createServiceWithAssignments(LocalDate serviceDate, LocalDate practiceDate, String location, 
                                           List<String> directorIds, List<MusicianAssignment> musicianAssignments);
    List<UserModel> getAllMusicians();
    ServiceModel assignDirectorsToService(String serviceId, List<String> directorIds);
    ServiceModel assignMusiciansToService(String serviceId, List<MusicianAssignment> musicianAssignments);
    ServiceModel updateServiceAssignments(String serviceId, UpdateAssingmentRequest request);
    
    // Actualización general de servicios
    ServiceModel updateService(ServiceModel service);
    
    // Historias de usuario del Director
    ServiceModel createSongListForService(String serviceId, String directorId, List<CreateSongListRequest> songs);
    ServiceModel updateSongListForService(String serviceId, String directorId, List<CreateSongListRequest> songs);
    
    // Consultas generales
    ServiceModel getServiceById(String serviceId);
    List<ServiceModel> getAllServices();
    List<ServiceModel> getServicesByDirector(String directorId);
    List<ServiceModel> getServicesByMusician(String musicianId);
    List<ServiceModel> getServicesByDate(LocalDate date);
    List<ServiceModel> getServicesByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Validaciones de roles dinámicos
    boolean isUserDirectorOfService(String userId, String serviceId);
    boolean isUserMusicianOfService(String userId, String serviceId);
    boolean isUserAdmin(String userId);
    
    // Eliminación automática (historia del sistema)
    void deleteExpiredServices();
    
   
} 