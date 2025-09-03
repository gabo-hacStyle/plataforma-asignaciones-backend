package com.backend.infraestructure.adapters.in.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.application.IAuthService;
import com.backend.domain.model.AuthUser;
import com.backend.domain.model.UserModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final IAuthService authService;
    
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> authenticateWithGoogle(@RequestBody GoogleAuthRequest request) {
        try {

            log.info("Request: {}", request);
            
            log.info("Type of request.getToken(): {}", request.getToken().getClass());

            // Autenticar con Google
            AuthUser authUser = authService.authenticateWithGoogle(request.getToken());

            log.info("AuthUser: {}", authUser);
            if (authUser.isNewUser()) {
                // Usuario nuevo - registrarlo
                UserModel newUser = authService.registerNewUser(authUser);
                String jwtToken = authService.generateJwtToken(newUser);
                
                AuthResponse response = new AuthResponse();
                response.setToken(jwtToken);
                response.setUser(newUser);
                response.setNewUser(true);
                response.setMessage("Usuario registrado exitosamente");
                
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                // Usuario existente - autenticarlo
                UserModel existingUser = authService.getUserByEmail(authUser.getEmail());
                String jwtToken = authService.generateJwtToken(existingUser);
                
                AuthResponse response = new AuthResponse();
                response.setToken(jwtToken);
                response.setUser(existingUser);
                response.setNewUser(false);
                response.setMessage("Usuario autenticado exitosamente");
                
                return ResponseEntity.ok(response);
            }
            
        } catch (IllegalArgumentException e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Error de autenticación: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Clases de request y response
    
    public static class GoogleAuthRequest {
        private String token;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
    
    public static class AuthResponse {
        private String token;
        private UserModel user;
        private boolean newUser;
        private String message;
        
        // Getters y setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public UserModel getUser() { return user; }
        public void setUser(UserModel user) { this.user = user; }
        
        public boolean isNewUser() { return newUser; }
        public void setNewUser(boolean newUser) { this.newUser = newUser; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
} 