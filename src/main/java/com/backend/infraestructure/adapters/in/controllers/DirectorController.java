package com.backend.infraestructure.adapters.in.controllers;

import java.util.List;

import com.backend.application.IDirectorService;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class DirectorController {
    
    private final IServiceService serviceService;
    private final IDirectorService directorService;
    
    @PostMapping("/{directorId}/services/{serviceId}/songs")
    public ResponseEntity<ServiceModel> createSongList(
            @PathVariable String directorId,
            @PathVariable String serviceId,
            @RequestBody List<CreateSongListRequest> songs) {
        try {

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

            if (!serviceService.isUserDirectorOfService(directorId, serviceId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            ServiceModel updatedService = serviceService.updateSongListForService(serviceId, directorId, songs);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{directorId}/services/{serviceId}/clothes")
    public ResponseEntity<ServiceModel> updateClothesColor(
            @PathVariable String directorId,
            @PathVariable String serviceId,
            @RequestBody String clothesColor) {
        try {

            if (!serviceService.isUserDirectorOfService(directorId, serviceId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            ServiceModel updatedService = directorService.updateClothesColorForService(serviceId, clothesColor);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            log.info("❌ Error actualizando color de ropa  {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}