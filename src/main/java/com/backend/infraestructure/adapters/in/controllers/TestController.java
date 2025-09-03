package com.backend.infraestructure.adapters.in.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    
    // Endpoint público (sin autenticación)
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Este es un endpoint público - no requiere autenticación");
    }
    
    // Endpoint que requiere autenticación (cualquier usuario)
    @GetMapping("/authenticated")
    public ResponseEntity<String> authenticatedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String authorities = auth.getAuthorities().toString();
        
        return ResponseEntity.ok("Usuario autenticado: " + userEmail + 
                               "\nRoles: " + authorities);
    }
    
    // Endpoint que requiere rol ADMIN
    @GetMapping("/admin")
    public ResponseEntity<String> adminEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String authorities = auth.getAuthorities().toString();
        
        return ResponseEntity.ok("¡Acceso autorizado como ADMIN!\n" +
                               "Usuario: " + userEmail + 
                               "\nRoles: " + authorities);
    }
} 