package com.backend.infraestructure.adapters.in.controllers;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.application.IServiceService;
import com.backend.application.dto.CreateServiceRequest;
import com.backend.application.dto.UpdateAssingmentRequest;
import com.backend.domain.model.ServiceModel;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final IServiceService serviceService;
    
    // Endpoint simplificado para crear servicios con asignaciones completas
    
    @PostMapping
    public ResponseEntity<ServiceModel> createServiceWithAssignments(@RequestBody CreateServiceRequest request) {
        try {
           
            
            
            ServiceModel createdService = serviceService.createServiceWithAssignments(
                request.getServiceDate(),
                request.getPracticeDate(),
                request.getLocation(),
                request.getDirectorIds(),
                request.getMusicianAssignments()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{serviceId}/assignments")
    public ResponseEntity<ServiceModel> updateServiceAssignments(
            @PathVariable String serviceId,
            @RequestBody UpdateAssingmentRequest request) {
        try {
            ServiceModel updatedService = serviceService.updateServiceAssignments(
                serviceId, request);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // Put general
    @PutMapping("/{serviceId}")
    public ResponseEntity<ServiceModel> updateService(@PathVariable String serviceId, @RequestBody ServiceModel service) {
        try {
            service.setId(serviceId);
            ServiceModel updatedService = serviceService.updateService(service);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }





            //@PostMapping("/{serviceId}/directors")
    //public ResponseEntity<ServiceModel> assignDirectorsToService(
    //        @PathVariable String serviceId,
    //        @RequestBody List<String> directorIds) {
    //    try {
    //        ServiceModel updatedService = serviceService.assignDirectorsToService(serviceId, directorIds);
    //        return ResponseEntity.ok(updatedService);
    //    } catch (IllegalArgumentException e) {
    //        return ResponseEntity.badRequest().build();
    //    }
    //}
    //@PostMapping("/{serviceId}/musicians")
    //public ResponseEntity<ServiceModel> assignMusiciansToService(
    //        @PathVariable String serviceId,
    //        @RequestBody AssignMusiciansRequest request) {
    //    try {
    //        ServiceModel updatedService = serviceService.assignMusiciansToService(
    //            serviceId, request.getMusicianIds(), request.getInstruments());
    //        return ResponseEntity.ok(updatedService);
    //    } catch (IllegalArgumentException e) {
    //        return ResponseEntity.badRequest().build();
    //    }
    //}
    
    
    
    
    
    
    
   
    
    //public static class AssignMusiciansRequest {
    //    private List<String> musicianIds;
    //    private List<String> instruments;
    //    
    //    // Getters y setters
    //    public List<String> getMusicianIds() { return musicianIds; }
    //    public void setMusicianIds(List<String> musicianIds) { this.musicianIds = musicianIds; }
    //    public List<String> getInstruments() { return instruments; }
    //    public void setInstruments(List<String> instruments) { this.instruments = instruments; }
    //}


  
} 