package com.backend.application.implementations;

import java.time.LocalDateTime;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backend.application.IAuthService;
import com.backend.application.JwtService;
import com.backend.domain.model.AuthUser;
import com.backend.domain.model.UserModel;
import com.backend.domain.port.UserUseCases;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    
    private final UserUseCases userUseCases;
    
    private final JwtService jwtService;
    
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Value("${google.oauth.client-id}")
    private String googleClientId;
    
    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AuthUser authenticateWithGoogle(String googleToken) {
        try {

            log.info("Google Token en el servicio: {}", googleToken);
            log.info("Type of Google Token en el servicio: {}", googleToken.getClass());

            // Validar el token con Google
            GoogleUserInfo googleUserInfo = validateGoogleToken(googleToken);
            
            // Verificar si el usuario existe
            UserModel existingUser = userUseCases.getUserByEmail(googleUserInfo.getEmail());
            
            if (existingUser != null) {
                // Usuario existente
                AuthUser authUser = new AuthUser();
                authUser.setId(existingUser.getId());
                authUser.setEmail(existingUser.getEmail());
                authUser.setName(existingUser.getName());
                authUser.setGoogleId(googleUserInfo.getGoogleId());
                authUser.setPicture(googleUserInfo.getPicture());
                authUser.setNewUser(false);
                authUser.setCreatedAt(existingUser.getCreatedAt());
                authUser.setLastLoginAt(LocalDateTime.now());
                
                return authUser;
            } else {
                // Usuario nuevo
                AuthUser authUser = new AuthUser(googleUserInfo.getEmail(), googleUserInfo.getGoogleId());
                authUser.setName(googleUserInfo.getName());
                authUser.setPicture(googleUserInfo.getPicture());
                authUser.setNewUser(true);
                
                return authUser;
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Token de Google inválido: " + e.getMessage());
        }
    }
    
    @Override
    public UserModel registerNewUser(AuthUser authUser) {
        // Crear nuevo usuario con datos mínimos
        UserModel newUser = new UserModel();
        newUser.setEmail(authUser.getEmail());
        newUser.setRole(UserModel.Role.MUSICIAN); // Rol por defecto
        newUser.setCreatedAt(LocalDateTime.now());
        
        // No establecer nombre ni teléfono - el usuario los completará después
        // newUser.setName(authUser.getName()); // Comentado para que el usuario lo complete después
        // newUser.setPhoneNumber(null); // Comentado para que el usuario lo complete después
        
        return userUseCases.createUser(newUser);
    }
    
    @Override
    public boolean userExistsByEmail(String email) {
        return userUseCases.getUserByEmail(email) != null;
    }
    
    @Override
    public UserModel getUserByEmail(String email) {
        return userUseCases.getUserByEmail(email);
    }
    
    @Override
    public String generateJwtToken(UserModel user) {
        return jwtService.generateToken(user);
    }
    
    @Override
    public UserModel validateJwtToken(String token) {
        try {
            String email = jwtService.extractEmail(token);
            String userId = jwtService.extractUserId(token);
            
            if (jwtService.validateToken(token, email)) {
                return userUseCases.getUserById(userId);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Valida el ID Token de Google con la API de Google y extrae la información del usuario
     */
    private GoogleUserInfo validateGoogleToken(String token) throws Exception {
        try {
            log.info("Token en el servicio de validateGoogleToken: {}", token);
            
            // Para ID Tokens de Google, usamos la API de userinfo
            // Primero validamos el token con Google
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + token;
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            log.info("Response: {}", response);
            log.info("Type of Response: {}", response.getClass());
            log.info("Response Body: {}", response.getBody());
            log.info("Response Status Code (boolean): {}", response.getStatusCode().is2xxSuccessful());
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                // Validar que el token pertenece a nuestra aplicación
                String aud = jsonNode.get("aud").asText();
                if (!aud.equals(googleClientId)) {
                    throw new IllegalArgumentException("Token no válido para esta aplicación. Expected: " + googleClientId + ", Got: " + aud);
                }
                
                // Extraer información del usuario
                String email = jsonNode.get("email").asText();
                String googleId = jsonNode.get("sub").asText();
                String name = jsonNode.has("name") ? jsonNode.get("name").asText() : "";
                String picture = jsonNode.has("picture") ? jsonNode.get("picture").asText() : "";
                
                log.info("Email: {}", email);
                log.info("Google ID: {}", googleId);
                log.info("Name: {}", name);
                log.info("Picture: {}", picture);
                
                return new GoogleUserInfo(email, googleId, name, picture);
            } else {
                log.error("Error response from Google: {}", response.getBody());
                throw new IllegalArgumentException("Error al validar token con Google: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error validating Google token: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error al validar token de Google: " + e.getMessage());
        }
    }
    
    /**
     * Clase interna para encapsular la información del usuario de Google
     */
    private static class GoogleUserInfo {
        private final String email;
        private final String googleId;
        private final String name;
        private final String picture;
        
        public GoogleUserInfo(String email, String googleId, String name, String picture) {
            this.email = email;
            this.googleId = googleId;
            this.name = name;
            this.picture = picture;
        }
        
        public String getEmail() { return email; }
        public String getGoogleId() { return googleId; }
        public String getName() { return name; }
        public String getPicture() { return picture; }
    }
} 