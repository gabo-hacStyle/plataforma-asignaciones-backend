package com.backend.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.application.implementations.UserServiceImpl;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;
import com.backend.domain.port.ServicesUseCases;
import com.backend.domain.port.UserUseCases;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserUseCases userUseCases;
    
    @Mock
    private ServicesUseCases servicesUseCases;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private UserModel testAdmin;
    private UserModel testDirector;
    private UserModel testMusician;
    private ServiceModel testService;
    
    @BeforeEach
    void setUp() {
        testAdmin = new UserModel();
        testAdmin.setId("admin1");
        testAdmin.setName("Admin Test");
        testAdmin.setEmail("admin@test.com");
        testAdmin.setRole(UserModel.Role.ADMIN);
        testAdmin.setCreatedAt(LocalDateTime.now());
        
        testDirector = new UserModel();
        testDirector.setId("director1");
        testDirector.setName("Director Test");
        testDirector.setEmail("director@test.com");
        testDirector.setRole(UserModel.Role.DIRECTOR);
        testDirector.setCreatedAt(LocalDateTime.now());
        
        testMusician = new UserModel();
        testMusician.setId("musician1");
        testMusician.setName("Musician Test");
        testMusician.setEmail("musician@test.com");
        testMusician.setRole(UserModel.Role.MUSICIAN);
        testMusician.setCreatedAt(LocalDateTime.now());
        
        testService = new ServiceModel();
        testService.setId("service1");
        testService.setDirectors(Arrays.asList(testDirector));
    }
    
    // Tests para operaciones CRUD básicas
    
    @Test
    void testCreateUser_ShouldCreateNewUser() {
        // Given
        when(userUseCases.createUser(any(UserModel.class))).thenReturn(testMusician);
        
        // When
        UserModel result = userService.createUser(testMusician);
        
        // Then
        assertNotNull(result);
        assertEquals(testMusician.getId(), result.getId());
        verify(userUseCases).createUser(testMusician);
    }
    
    @Test
    void testCreateUser_ShouldSetDefaultRoleAsMusician() {
        // Given
        UserModel newUser = new UserModel();
        newUser.setName("New User");
        newUser.setEmail("new@test.com");
        newUser.setRole(null); // Sin rol específico
        
        when(userUseCases.createUser(any(UserModel.class))).thenReturn(newUser);
      
        
        // Then
        assertEquals(UserModel.Role.MUSICIAN, newUser.getRole());
        verify(userUseCases).createUser(newUser);
    }
    
    @Test
    void testCreateUser_ShouldThrowException_WhenUserIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(null);
        });
    }
    
    @Test
    void testCreateUser_ShouldThrowException_WhenNameIsEmpty() {
        // Given
        UserModel invalidUser = new UserModel();
        invalidUser.setName("");
        invalidUser.setEmail("test@test.com");
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(invalidUser);
        });
    }
    
    @Test
    void testCreateUser_ShouldThrowException_WhenEmailIsInvalid() {
        // Given
        UserModel invalidUser = new UserModel();
        invalidUser.setName("Test User");
        invalidUser.setEmail("invalid-email");
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(invalidUser);
        });
    }
    
    @Test
    void testGetUserById_ShouldReturnUser() {
        // Given
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        
        // When
        UserModel result = userService.getUserById("musician1");
        
        // Then
        assertNotNull(result);
        assertEquals("musician1", result.getId());
        verify(userUseCases).getUserById("musician1");
    }
    
    @Test
    void testGetUserById_ShouldThrowException_WhenIdIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(null);
        });
    }
    
    @Test
    void testGetUserByEmail_ShouldReturnUser() {
        // Given
        when(userUseCases.getUserByEmail("musician@test.com")).thenReturn(testMusician);
        
        // When
        UserModel result = userService.getUserByEmail("musician@test.com");
        
        // Then
        assertNotNull(result);
        assertEquals("musician@test.com", result.getEmail());
        verify(userUseCases).getUserByEmail("musician@test.com");
    }
    
    @Test
    void testGetUserByEmail_ShouldThrowException_WhenEmailIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserByEmail(null);
        });
    }
    
    @Test
    void testUpdateUser_ShouldUpdateUser() {
        // Given
        testMusician.setName("Musician Updated");
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        when(userUseCases.updateUser(any(UserModel.class))).thenReturn(testMusician);
        
        // When
        UserModel result = userService.updateUser(testMusician);
        
        // Then
        assertNotNull(result);
        assertEquals("Musician Updated", result.getName());
        verify(userUseCases).updateUser(testMusician);
    }
    
    @Test
    void testUpdateUser_ShouldUpdatePartialUser() {
        // Given
        UserModel partialUpdate = new UserModel();
        partialUpdate.setId("musician1");
        partialUpdate.setName("Only Name Updated");
        // No email, phone, or role specified - should only update name
        
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        when(userUseCases.updateUser(any(UserModel.class))).thenReturn(testMusician);
        
        // When
        UserModel result = userService.updateUser(partialUpdate);
        
        // Then
        assertNotNull(result);
        verify(userUseCases).updateUser(any(UserModel.class));
    }
    
    @Test
    void testUpdateUser_ShouldUpdateOnlyEmail() {
        // Given
        UserModel partialUpdate = new UserModel();
        partialUpdate.setId("musician1");
        partialUpdate.setEmail("newemail@test.com");
        // Only email specified
        
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        when(userUseCases.updateUser(any(UserModel.class))).thenReturn(testMusician);
        
        // When
        UserModel result = userService.updateUser(partialUpdate);
        
        // Then
        assertNotNull(result);
        verify(userUseCases).updateUser(any(UserModel.class));
    }
    
    @Test
    void testUpdateUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Given
        when(userUseCases.getUserById("nonexistent")).thenReturn(null);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            UserModel nonExistentUser = new UserModel();
            nonExistentUser.setId("nonexistent");
            nonExistentUser.setName("Test");
            nonExistentUser.setEmail("test@test.com");
            userService.updateUser(nonExistentUser);
        });
    }
    
    @Test
    void testDeleteUser_ShouldDeleteUser() {
        // Given
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        doNothing().when(userUseCases).deleteUser("musician1");
        
        // When
        userService.deleteUser("musician1");
        
        // Then
        verify(userUseCases).deleteUser("musician1");
    }
    
    @Test
    void testGetAllUsers_ShouldReturnAllUsers() {
        // Given
        List<UserModel> expectedUsers = Arrays.asList(testAdmin, testDirector, testMusician);
        when(userUseCases.getAllUsers()).thenReturn(expectedUsers);
        
        // When
        List<UserModel> result = userService.getAllUsers();
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(userUseCases).getAllUsers();
    }
    
    @Test
    void testGetUserByPhoneNumber_ShouldReturnUser() {
        // Given
        testMusician.setPhoneNumber("123456789");
        when(userUseCases.getUserByPhoneNumber("123456789")).thenReturn(testMusician);
        
        // When
        UserModel result = userService.getUserByPhoneNumber("123456789");
        
        // Then
        assertNotNull(result);
        assertEquals("123456789", result.getPhoneNumber());
        verify(userUseCases).getUserByPhoneNumber("123456789");
    }
    
    @Test
    void testGetUserByPhoneNumber_ShouldThrowException_WhenPhoneIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserByPhoneNumber(null);
        });
    }
    
    // Tests para funcionalidades específicas de las historias de usuario
    
    @Test
    void testGetUsersByRole_ShouldReturnUsersByRole() {
        // Given
        List<UserModel> allUsers = Arrays.asList(testAdmin, testDirector, testMusician);
        when(userUseCases.getAllUsers()).thenReturn(allUsers);
        
        // When
        List<UserModel> result = userService.getUsersByRole(UserModel.Role.MUSICIAN);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(UserModel.Role.MUSICIAN, result.get(0).getRole());
    }
    
    @Test
    void testGetUsersByRole_ShouldThrowException_WhenRoleIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUsersByRole(null);
        });
    }
    
    @Test
    void testGetAvailableMusicians_ShouldReturnAvailableMusicians() {
        // Given
        List<UserModel> allUsers = Arrays.asList(testAdmin, testDirector, testMusician);
        when(userUseCases.getAllUsers()).thenReturn(allUsers);
        
        // When
        List<UserModel> result = userService.getAvailableMusicians();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(UserModel.Role.MUSICIAN, result.get(0).getRole());
    }
    
    @Test
    void testGetAvailableDirectors_ShouldReturnAvailableDirectors() {
        // Given
        List<UserModel> allUsers = Arrays.asList(testAdmin, testDirector, testMusician);
        when(userUseCases.getAllUsers()).thenReturn(allUsers);
        
        // When
        List<UserModel> result = userService.getAvailableDirectors();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(UserModel.Role.DIRECTOR, result.get(0).getRole());
    }
    
    // Tests para consultas de servicios por usuario
    
    @Test
    void testGetServicesForUser_ShouldReturnUserServices() {
        // Given
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        List<ServiceModel> directorServices = Arrays.asList(testService);
        List<ServiceModel> musicianServices = Arrays.asList(testService);
        when(servicesUseCases.getServicesByDirector("musician1")).thenReturn(directorServices);
        when(servicesUseCases.getServicesByMusician("musician1")).thenReturn(musicianServices);
        
        // When
        List<ServiceModel> result = userService.getServicesForUser("musician1");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Debería eliminar duplicados
        verify(servicesUseCases).getServicesByDirector("musician1");
        verify(servicesUseCases).getServicesByMusician("musician1");
    }
    
    @Test
    void testGetServicesForUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Given
        when(userUseCases.getUserById("nonexistent")).thenReturn(null);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getServicesForUser("nonexistent");
        });
    }
    
    @Test
    void testGetUpcomingServicesForUser_ShouldReturnUpcomingServices() {
        // Given
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        testService.setServiceDate(LocalDate.now().plusDays(7));
        List<ServiceModel> allServices = Arrays.asList(testService);
        when(servicesUseCases.getServicesByDirector("musician1")).thenReturn(allServices);
        when(servicesUseCases.getServicesByMusician("musician1")).thenReturn(allServices);
        
        // When
        List<ServiceModel> result = userService.getUpcomingServicesForUser("musician1");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    // Tests para validaciones
    
    @Test
    void testIsUserMusician_ShouldReturnTrueForMusician() {
        // Given
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        
        // When
        boolean result = userService.isUserMusician("musician1");
        
        // Then
        assertTrue(result);
        verify(userUseCases).getUserById("musician1");
    }
    
    @Test
    void testIsUserMusician_ShouldReturnFalseForNonMusician() {
        // Given
        when(userUseCases.getUserById("director1")).thenReturn(testDirector);
        
        // When
        boolean result = userService.isUserMusician("director1");
        
        // Then
        assertFalse(result);
        verify(userUseCases).getUserById("director1");
    }
    
    @Test
    void testIsUserMusician_ShouldReturnFalseForNullUserId() {
        // When
        boolean result = userService.isUserMusician(null);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testIsUserDirector_ShouldReturnTrueForDirector() {
        // Given
        when(userUseCases.getUserById("director1")).thenReturn(testDirector);
        
        // When
        boolean result = userService.isUserDirector("director1");
        
        // Then
        assertTrue(result);
        verify(userUseCases).getUserById("director1");
    }
    
    @Test
    void testIsUserDirector_ShouldReturnFalseForNonDirector() {
        // Given
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        
        // When
        boolean result = userService.isUserDirector("musician1");
        
        // Then
        assertFalse(result);
        verify(userUseCases).getUserById("musician1");
    }
    
    @Test
    void testIsUserAdmin_ShouldReturnTrueForAdmin() {
        // Given
        when(userUseCases.getUserById("admin1")).thenReturn(testAdmin);
        
        // When
        boolean result = userService.isUserAdmin("admin1");
        
        // Then
        assertTrue(result);
        verify(userUseCases).getUserById("admin1");
    }
    
    @Test
    void testIsUserAdmin_ShouldReturnFalseForNonAdmin() {
        // Given
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        
        // When
        boolean result = userService.isUserAdmin("musician1");
        
        // Then
        assertFalse(result);
        verify(userUseCases).getUserById("musician1");
    }
} 