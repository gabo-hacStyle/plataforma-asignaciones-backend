package com.backend.infraestructure.adapters.in.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.application.IServiceService;
import com.backend.infraestructure.adapters.in.controllers.dto.SystemHealthResponse;
import com.backend.infraestructure.adapters.in.controllers.dto.SystemOperationResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SystemController {
    
    private final IServiceService serviceService;
    
    // Operaciones de mantenimiento del sistema
    
    @DeleteMapping("/services/expired")
    public ResponseEntity<SystemOperationResponse> deleteExpiredServices() {
        try {
            serviceService.deleteExpiredServices();
            
            SystemOperationResponse response = new SystemOperationResponse();
            response.setOperation("deleteExpiredServices");
            response.setStatus("SUCCESS");
            response.setMessage("Servicios expirados eliminados correctamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SystemOperationResponse response = new SystemOperationResponse();
            response.setOperation("deleteExpiredServices");
            response.setStatus("ERROR");
            response.setMessage("Error al eliminar servicios expirados: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // Health check del sistema
    
    @GetMapping("/health")
    public ResponseEntity<SystemHealthResponse> healthCheck() {
        try {
            SystemHealthResponse health = new SystemHealthResponse();
            health.setStatus("UP");
            health.setTimestamp(java.time.LocalDateTime.now().toString());
            health.setVersion("1.0.0");
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            SystemHealthResponse health = new SystemHealthResponse();
            health.setStatus("DOWN");
            health.setTimestamp(java.time.LocalDateTime.now().toString());
            health.setVersion("1.0.0");
            health.setError(e.getMessage());
            
            return ResponseEntity.internalServerError().body(health);
        }
    }
    
    // Información del sistema
    
    @GetMapping("/info")
    public ResponseEntity<SystemInfoResponse> getSystemInfo() {
        try {
            SystemInfoResponse info = new SystemInfoResponse();
            info.setApplicationName("Plataforma Iglesia");
            info.setVersion("1.0.0");
            info.setEnvironment("Development");
            info.setJavaVersion(System.getProperty("java.version"));
            info.setSpringVersion("3.5.4");
            info.setDatabase("MongoDB");
            
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Clases de response
    

    
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SystemInfoResponse {
        private String applicationName;
        private String version;
        private String environment;
        private String javaVersion;
        private String springVersion;
        private String database;
    }
} 