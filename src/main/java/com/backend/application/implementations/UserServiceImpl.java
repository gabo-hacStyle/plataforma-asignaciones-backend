package com.backend.application.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.application.IUserService;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;
import com.backend.domain.port.ServicesUseCases;
import com.backend.domain.port.UserUseCases;

@Service
public class UserServiceImpl implements IUserService {
    
    @Autowired
    private UserUseCases userUseCases;
    
    @Autowired
    private ServicesUseCases servicesUseCases;
    
    // Operaciones CRUD básicas
    @Override
    public UserModel createUser(UserModel user) {
        validateUserData(user);
        
        // Establecer rol por defecto como MUSICIAN si no se especifica
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of(UserModel.Role.MUSICIAN));
        }
        
        // Establecer fecha de creación si no existe
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        
        return userUseCases.createUser(user);
    }
    
    @Override
    public UserModel getUserById(String id) {
        validateUserId(id);
        return userUseCases.getUserById(id);
    }
    
    @Override
    public UserModel getUserByEmail(String email) {
        validateEmail(email);
        return userUseCases.getUserByEmail(email);
    }
    
    @Override
    public UserModel updateUser(UserModel user) {
        validateUserId(user.getId());
        validateUserExists(user.getId());
        
        // Obtener el usuario existente
        UserModel existingUser = userUseCases.getUserById(user.getId());
        
        // Actualizar solo los campos que no son null
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (!isValidEmail(user.getEmail())) {
                throw new IllegalArgumentException("Formato de email inválido");
            }
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getRoles() != null) {
            existingUser.setRoles(user.getRoles());
        }
        
        return userUseCases.updateUser(existingUser);
    }
    
    @Override
    public void deleteUser(String id) {
        validateUserId(id);
        validateUserExists(id);
        
        userUseCases.deleteUser(id);
    }
    
    @Override
    public List<UserModel> getAllUsers() {
        return userUseCases.getAllUsers();
    }
    
    @Override
    public UserModel getUserByPhoneNumber(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        return userUseCases.getUserByPhoneNumber(phoneNumber);
    }
    
    // Funcionalidades específicas para las historias de usuario
    @Override
    public List<UserModel> getUsersByRole(UserModel.Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Rol no puede ser null");
        }
        
        List<UserModel> allUsers = userUseCases.getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(role))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserModel> getAvailableMusicians() {
        List<UserModel> allUsers = userUseCases.getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(UserModel.Role.MUSICIAN))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserModel> getAvailableDirectors() {
        List<UserModel> allUsers = userUseCases.getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(UserModel.Role.DIRECTOR))
                .collect(Collectors.toList());
    }
    
    // Consultas de servicios por usuario
    @Override
    public List<ServiceModel> getServicesForUser(String userId) {
        validateUserId(userId);
        validateUserExists(userId);
        
        // Obtener servicios donde el usuario es director o músico
        List<ServiceModel> directorServices = servicesUseCases.getServicesByDirector(userId);
        List<ServiceModel> musicianServices = servicesUseCases.getServicesByMusician(userId);
        
        // Combinar y eliminar duplicados
        List<ServiceModel> allServices = new java.util.ArrayList<>(directorServices);
        for (ServiceModel service : musicianServices) {
            if (!allServices.stream().anyMatch(s -> s.getId().equals(service.getId()))) {
                allServices.add(service);
            }
        }
        
        return allServices;
    }
    
    @Override
    public List<ServiceModel> getUpcomingServicesForUser(String userId) {
        validateUserId(userId);
        validateUserExists(userId);
        
        List<ServiceModel> userServices = getServicesForUser(userId);
        LocalDate today = LocalDate.now();
        
        return userServices.stream()
                .filter(service -> service.getServiceDate() != null && 
                                 !service.getServiceDate().isBefore(today))
                .sorted((s1, s2) -> s1.getServiceDate().compareTo(s2.getServiceDate()))
                .collect(Collectors.toList());
    }
    
    // Validaciones
    @Override
    public boolean isUserMusician(String userId) {
        if (userId == null) {
            return false;
        }
        
        UserModel user = userUseCases.getUserById(userId);
        return user != null && user.getRoles() != null && user.getRoles().contains(UserModel.Role.MUSICIAN);
    }
    
    @Override
    public boolean isUserDirector(String userId) {
        if (userId == null) {
            return false;
        }
        
        UserModel user = userUseCases.getUserById(userId);
        return user != null && user.getRoles() != null && user.getRoles().contains(UserModel.Role.DIRECTOR);
    }
    
    @Override
    public boolean isUserAdmin(String userId) {
        if (userId == null) {
            return false;
        }
        
        UserModel user = userUseCases.getUserById(userId);
        return user != null && user.getRoles() != null && user.getRoles().contains(UserModel.Role.ADMIN);
    }
    
    // Métodos privados para validación y optimización
    
    private void validateUserData(UserModel user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de usuario es obligatorio");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email de usuario es obligatorio");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }
    
    private void validateUserId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de usuario no puede ser null o vacío");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email no puede ser null o vacío");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }
    
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Número de teléfono no puede ser null o vacío");
        }
    }
    
    private void validateUserExists(String userId) {
        UserModel existingUser = userUseCases.getUserById(userId);
        if (existingUser == null) {
            throw new IllegalArgumentException("Usuario no encontrado: " + userId);
        }
    }
    
    private boolean isValidEmail(String email) {
        // Validación básica de email
        return email != null && email.contains("@") && email.contains(".");
    }
}
