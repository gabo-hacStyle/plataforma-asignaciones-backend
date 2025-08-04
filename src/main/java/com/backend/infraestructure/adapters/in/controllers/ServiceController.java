package com.backend.infraestructure.adapters.in.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.application.IServiceService;
import com.backend.domain.model.ServiceModel;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceController {
    
    private final IServiceService serviceService;
    
    // Endpoint simplificado para crear servicio con asignaciones
   
    // Consultas generales
    
    @GetMapping("/{id}")
    public ResponseEntity<ServiceModel> getServiceById(@PathVariable String id) {
        try {
            ServiceModel service = serviceService.getServiceById(id);
            if (service != null) {
                return ResponseEntity.ok(service);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ServiceModel>> getAllServices() {
        List<ServiceModel> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }
    
    
    
 
    
    // Validaciones de roles dinámicos
    
   // @GetMapping("/{serviceId}/director/{userId}/is-director")
   // public ResponseEntity<Boolean> isUserDirectorOfService(
   //         @PathVariable String serviceId,
   //         @PathVariable String userId) {
   //     boolean isDirector = serviceService.isUserDirectorOfService(userId, serviceId);
   //     return ResponseEntity.ok(isDirector);
   // }
   // 
   // @GetMapping("/{serviceId}/musician/{userId}/is-musician")
   // public ResponseEntity<Boolean> isUserMusicianOfService(
   //         @PathVariable String serviceId,
   //         @PathVariable String userId) {
   //     boolean isMusician = serviceService.isUserMusicianOfService(userId, serviceId);
   //     return ResponseEntity.ok(isMusician);
   // }
   // 
   // @GetMapping("/{userId}/is-admin")
   // public ResponseEntity<Boolean> isUserAdmin(@PathVariable String userId) {
   //     boolean isAdmin = serviceService.isUserAdmin(userId);
   //     return ResponseEntity.ok(isAdmin);
   // }
   // 
   // // Eliminación automática (historia del sistema)
   // 
   // @DeleteMapping("/expired")
   // public ResponseEntity<Void> deleteExpiredServices() {
   //     serviceService.deleteExpiredServices();
   //     return ResponseEntity.noContent().build();
   // }
   // 
   
   // Clases de request para encapsular datos

   public static class CreateServiceWithAssignmentsRequest {
        private LocalDate serviceDate;
        private LocalDate practiceDate;
        private String location;
        private List<String> directorIds;
        private List<MusicianAssignment> musicianAssignments;
        
        // Getters y setters
        public LocalDate getServiceDate() { return serviceDate; }
        public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
        public LocalDate getPracticeDate() { return practiceDate; }
        public void setPracticeDate(LocalDate practiceDate) { this.practiceDate = practiceDate; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public List<String> getDirectorIds() { return directorIds; }
        public void setDirectorIds(List<String> directorIds) { this.directorIds = directorIds; }
        public List<MusicianAssignment> getMusicianAssignments() { return musicianAssignments; }
        public void setMusicianAssignments(List<MusicianAssignment> musicianAssignments) { this.musicianAssignments = musicianAssignments; }
    }
    
    public static class MusicianAssignment {
        private String musicianId;
        private String instrument;
        
        // Getters y setters
        public String getMusicianId() { return musicianId; }
        public void setMusicianId(String musicianId) { this.musicianId = musicianId; }
        public String getInstrument() { return instrument; }
        public void setInstrument(String instrument) { this.instrument = instrument; }
    }
    
   
} 