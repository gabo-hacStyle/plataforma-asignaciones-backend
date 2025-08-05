package com.backend.infraestructure.adapters.in.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.application.IServiceService;
import com.backend.application.dto.CreateSongListRequest;
import com.backend.domain.model.ServiceModel;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/director")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DirectorController {
    
    private final IServiceService serviceService;
    
    @PostMapping("/{directorId}/services/{serviceId}/songs")
    public ResponseEntity<ServiceModel> createSongList(
            @PathVariable String directorId,
            @PathVariable String serviceId,
            @RequestBody List<CreateSongListRequest> songs) {
        try {
            // Verificar que el director tiene permisos sobre este servicio
            if (!serviceService.isUserDirectorOfService(directorId, serviceId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            ServiceModel updatedService = serviceService.createSongListForService(serviceId, directorId, songs);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{directorId}/services/{serviceId}/songs")
    public ResponseEntity<ServiceModel> updateSongList(
            @PathVariable String directorId,
            @PathVariable String serviceId,
            @RequestBody List<CreateSongListRequest> songs) {
        try {
            // Verificar que el director tiene permisos sobre este servicio
            if (!serviceService.isUserDirectorOfService(directorId, serviceId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            ServiceModel updatedService = serviceService.updateSongListForService(serviceId, directorId, songs);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Clases de response
    
    public static class DirectorPermissionsResponse {
        private String directorId;
        private String serviceId;
        private boolean isDirectorOfService;
        private boolean isAdmin;
        
        // Getters y setters
        public String getDirectorId() { return directorId; }
        public void setDirectorId(String directorId) { this.directorId = directorId; }
        public String getServiceId() { return serviceId; }
        public void setServiceId(String serviceId) { this.serviceId = serviceId; }
        public boolean isDirectorOfService() { return isDirectorOfService; }
        public void setIsDirectorOfService(boolean directorOfService) { isDirectorOfService = directorOfService; }
        public boolean isAdmin() { return isAdmin; }
        public void setIsAdmin(boolean admin) { isAdmin = admin; }
    }
} 