package com.backend.application;

import java.time.LocalDate;
import java.util.List;

import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;

public interface IServiceService {
    
    // Historias de usuario del Admin
    ServiceModel createService(ServiceModel service);
    ServiceModel createServiceWithAssignments(LocalDate serviceDate, LocalDate practiceDate, String location, 
                                           List<String> directorIds, List<MusicianAssignment> musicianAssignments);
    List<UserModel> getAllMusicians();
    ServiceModel assignDirectorsToService(String serviceId, List<String> directorIds);
    ServiceModel assignMusiciansToService(String serviceId, List<String> musicianIds, List<String> instruments);
    ServiceModel updateServiceAssignments(String serviceId, List<String> directorIds, List<String> musicianIds, List<String> instruments);
    
    // Historias de usuario del Director
    ServiceModel createSongListForService(String serviceId, String directorId, List<String> songNames, List<String> composers, 
                                       List<String> musicalLinks, List<String> tonalities);
    ServiceModel updateSongListForService(String serviceId, String directorId, List<String> songNames, List<String> composers, 
                                        List<String> musicalLinks, List<String> tonalities);
    
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
    
    // Clase para asignación de músicos
    public static class MusicianAssignment {
        private String musicianId;
        private String instrument;
        
        public MusicianAssignment() {}
        
        public MusicianAssignment(String musicianId, String instrument) {
            this.musicianId = musicianId;
            this.instrument = instrument;
        }
        
        // Getters y setters
        public String getMusicianId() { return musicianId; }
        public void setMusicianId(String musicianId) { this.musicianId = musicianId; }
        public String getInstrument() { return instrument; }
        public void setInstrument(String instrument) { this.instrument = instrument; }
    }
} 