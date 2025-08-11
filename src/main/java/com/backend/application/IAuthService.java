package com.backend.application;

import com.backend.domain.model.AuthUser;
import com.backend.domain.model.UserModel;

public interface IAuthService {
    
    /**
     * Autentica un usuario con Google OAuth
     * @param googleToken Token de Google
     * @return AuthUser con información del usuario autenticado
     */
    AuthUser authenticateWithGoogle(String googleToken);
    
    /**
     * Registra un nuevo usuario después de la autenticación con Google
     * @param authUser Usuario autenticado
     * @return UserModel del usuario registrado
     */
    UserModel registerNewUser(AuthUser authUser);
    
    /**
     * Verifica si un usuario existe por email
     * @param email Email del usuario
     * @return true si existe, false si no
     */
    boolean userExistsByEmail(String email);
    
    /**
     * Obtiene un usuario por email
     * @param email Email del usuario
     * @return UserModel del usuario
     */
    UserModel getUserByEmail(String email);
    
    /**
     * Genera un JWT token para el usuario
     * @param user Usuario para generar el token
     * @return JWT token
     */
    String generateJwtToken(UserModel user);
    
    /**
     * Valida un JWT token
     * @param token JWT token a validar
     * @return UserModel del usuario si el token es válido
     */
    UserModel validateJwtToken(String token);
} 