package com.backend.application;

import java.time.LocalDate;
import java.util.List;

import com.backend.application.dto.*;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;

public interface IServiceService {
    
    // Historias de usuario del Admin
    ServiceModel createService(ServiceModel service);
    ServiceModel createServiceWithAssignments(LocalDate serviceDate, LocalDate practiceDate, String location, 
                                           List<String> directorIds, List<MusicianAssignment> musicianAssignments);
    OCRResponseDTO createServiceWithOCR(List<OCRRequestInfoDTO> requests);

    ServiceModel assignDirectorsToService(String serviceId, List<String> directorIds);
    ServiceModel assignMusiciansToService(String serviceId, List<MusicianAssignment> musicianAssignments);
    ServiceModel updateServiceAssignments(String serviceId, UpdateAssingmentRequest request);
    

    ServiceModel updateService(ServiceModel service);
    

    ServiceModel createSongListForService(String serviceId, String directorId, List<CreateSongListRequest> songs);
    ServiceModel updateSongListForService(String serviceId, String directorId, List<CreateSongListRequest> songs);

    ServiceModel getServiceById(String serviceId);
    List<ServiceModel> getAllServices();

    
    // Validaciones de roles dinámicos
    boolean isUserDirectorOfService(String userId, String serviceId);
    
    

    void deleteExpiredServices();
    
   
} 