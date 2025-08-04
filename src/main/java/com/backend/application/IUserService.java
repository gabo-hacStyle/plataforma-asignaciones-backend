package com.backend.application;

import java.util.List;

import com.backend.domain.model.UserModel;
import com.backend.domain.model.ServiceModel;

public interface IUserService {
    
    // Operaciones CRUD básicas
    UserModel createUser(UserModel user);
    UserModel getUserById(String id);
    UserModel getUserByEmail(String email);
    UserModel updateUser(UserModel user);
    void deleteUser(String id);
    List<UserModel> getAllUsers();
    UserModel getUserByPhoneNumber(String phoneNumber);
    
    // Funcionalidades específicas para las historias de usuario
    List<UserModel> getUsersByRole(UserModel.Role role);
    List<UserModel> getAvailableMusicians();
    List<UserModel> getAvailableDirectors();
    
    // Consultas de servicios por usuario
    List<ServiceModel> getServicesForUser(String userId);
    List<ServiceModel> getUpcomingServicesForUser(String userId);
    
    // Validaciones
    boolean isUserMusician(String userId);
    boolean isUserDirector(String userId);
    boolean isUserAdmin(String userId);
}
