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

import com.backend.application.implementations.ServiceServiceImpl;
import com.backend.domain.model.MusiciansList;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;
import com.backend.domain.port.ServicesUseCases;
import com.backend.domain.port.UserUseCases;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServicesUseCases servicesUseCases;
    
    @Mock
    private UserUseCases userUseCases;
    
    @InjectMocks
    private ServiceServiceImpl serviceService;
    
    private ServiceModel testService;
    private UserModel testDirector;
    private UserModel testMusician;
    
    @BeforeEach
    void setUp() {
        testDirector = new UserModel();
        testDirector.setId("director1");
        testDirector.setName("Director Test");
        testDirector.setEmail("director@test.com");
        testDirector.setRole(UserModel.Role.DIRECTOR);
        
        testMusician = new UserModel();
        testMusician.setId("musician1");
        testMusician.setName("Musician Test");
        testMusician.setEmail("musician@test.com");
        testMusician.setRole(UserModel.Role.MUSICIAN);
        
        // Crear lista de músicos para el servicio
        MusiciansList musicianAssignment = new MusiciansList();
        musicianAssignment.setMusician(testMusician);
        musicianAssignment.setInstrument("Piano");
        
        testService = new ServiceModel();
        testService.setId("service1");
        testService.setServiceDate(LocalDate.now().plusDays(7));
        testService.setPracticeDate(LocalDate.now().plusDays(5));
        testService.setDirectors(Arrays.asList(testDirector)); // Cambiado a lista
        testService.setMusiciansList(Arrays.asList(musicianAssignment)); // Agregado músicos
        testService.setLocation("Iglesia Principal");
        testService.setCreatedAt(LocalDateTime.now());
    }
    
    // Tests para historias de usuario del Admin
    
    @Test
    void testCreateService_ShouldCreateNewService() {
        // Given
        when(servicesUseCases.createService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.createService(testService);
        
        // Then
        assertNotNull(result);
        assertEquals(testService.getId(), result.getId());
        verify(servicesUseCases).createService(testService);
    }
    
    @Test
    void testCreateService_ShouldThrowException_WhenServiceIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceService.createService(null);
        });
    }
    
    @Test
    void testGetAllMusicians_ShouldReturnAllMusicians() {
        // Given
        List<UserModel> allUsers = Arrays.asList(testDirector, testMusician);
        when(userUseCases.getAllUsers()).thenReturn(allUsers);
        
        // When
        List<UserModel> result = serviceService.getAllMusicians();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(UserModel.Role.MUSICIAN, result.get(0).getRole());
    }
    
    @Test
    void testAssignDirectorsToService_ShouldAssignDirectors() {
        // Given
        List<String> directorIds = Arrays.asList("director1", "director2");
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(userUseCases.getUserById("director1")).thenReturn(testDirector);
        when(userUseCases.getUserById("director2")).thenReturn(testDirector);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.assignDirectorsToService("service1", directorIds);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases).updateService(any(ServiceModel.class));
    }
    
    @Test
    void testAssignDirectorsToService_ShouldChangeMusicianToDirector() {
        // Given
        UserModel musicianAsDirector = new UserModel();
        musicianAsDirector.setId("musician2");
        musicianAsDirector.setRole(UserModel.Role.MUSICIAN);
        
        List<String> directorIds = Arrays.asList("musician2");
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(userUseCases.getUserById("musician2")).thenReturn(musicianAsDirector);
        when(userUseCases.updateUser(any(UserModel.class))).thenReturn(musicianAsDirector);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        serviceService.assignDirectorsToService("service1", directorIds);
        
        // Then
        verify(userUseCases).updateUser(argThat(user -> user.getRole() == UserModel.Role.DIRECTOR));
    }
    
    @Test
    void testAssignMusiciansToService_ShouldAssignMusicians() {
        // Given
        List<String> musicianIds = Arrays.asList("musician1", "musician2");
        List<String> instruments = Arrays.asList("Piano", "Guitarra");
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        when(userUseCases.getUserById("musician2")).thenReturn(testMusician);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.assignMusiciansToService("service1", musicianIds, instruments);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases).updateService(any(ServiceModel.class));
    }
    
    @Test
    void testUpdateServiceAssignments_ShouldUpdateAssignments() {
        // Given
        List<String> directorIds = Arrays.asList("director1");
        List<String> musicianIds = Arrays.asList("musician1");
        List<String> instruments = Arrays.asList("Piano");
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(userUseCases.getUserById("director1")).thenReturn(testDirector);
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.updateServiceAssignments("service1", directorIds, musicianIds, instruments);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases, times(2)).updateService(any(ServiceModel.class));
    }
    
    // Tests para historias de usuario del Director
    
    @Test
    void testCreateSongListForService_ShouldCreateSongList() {
        // Given
        List<String> songNames = Arrays.asList("Canción 1", "Canción 2");
        List<String> composers = Arrays.asList("Compositor 1", "Compositor 2");
        List<String> musicalLinks = Arrays.asList("https://youtube.com/1", "https://youtube.com/2");
        List<String> tonalities = Arrays.asList("Original", "-1/2");
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.createSongListForService("service1", "director1", songNames, composers, musicalLinks, tonalities);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases).updateService(any(ServiceModel.class));
    }
    
    @Test
    void testCreateSongListForService_ShouldThrowException_WhenNotDirector() {
        // Given
        List<String> songNames = Arrays.asList("Canción 1");
        List<String> composers = Arrays.asList("Compositor 1");
        List<String> musicalLinks = Arrays.asList("https://youtube.com/1");
        List<String> tonalities = Arrays.asList("Original");
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceService.createSongListForService("service1", "musician1", songNames, composers, musicalLinks, tonalities);
        });
    }
    
    @Test
    void testUpdateSongListForService_ShouldUpdateSongList() {
        // Given
        List<String> songNames = Arrays.asList("Canción Actualizada");
        List<String> composers = Arrays.asList("Compositor Actualizado");
        List<String> musicalLinks = Arrays.asList("https://youtube.com/updated");
        List<String> tonalities = Arrays.asList("Original");
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.updateSongListForService("service1", "director1", songNames, composers, musicalLinks, tonalities);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases).updateService(any(ServiceModel.class));
    }
    
    // Tests para consultas generales
    
    @Test
    void testGetServiceById_ShouldReturnService() {
        // Given
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.getServiceById("service1");
        
        // Then
        assertNotNull(result);
        assertEquals("service1", result.getId());
        verify(servicesUseCases).getServiceById("service1");
    }
    
    @Test
    void testGetServiceById_ShouldThrowException_WhenIdIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceService.getServiceById(null);
        });
    }
    
    @Test
    void testGetAllServices_ShouldReturnAllServices() {
        // Given
        List<ServiceModel> expectedServices = Arrays.asList(testService);
        when(servicesUseCases.getAllServices()).thenReturn(expectedServices);
        
        // When
        List<ServiceModel> result = serviceService.getAllServices();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(servicesUseCases).getAllServices();
    }
    
    @Test
    void testGetServicesByDirector_ShouldReturnDirectorServices() {
        // Given
        List<ServiceModel> expectedServices = Arrays.asList(testService);
        when(servicesUseCases.getServicesByDirector("director1")).thenReturn(expectedServices);
        
        // When
        List<ServiceModel> result = serviceService.getServicesByDirector("director1");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(servicesUseCases).getServicesByDirector("director1");
    }
    
    @Test
    void testGetServicesByMusician_ShouldReturnMusicianServices() {
        // Given
        List<ServiceModel> expectedServices = Arrays.asList(testService);
        when(servicesUseCases.getServicesByMusician("musician1")).thenReturn(expectedServices);
        
        // When
        List<ServiceModel> result = serviceService.getServicesByMusician("musician1");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(servicesUseCases).getServicesByMusician("musician1");
    }
    
    @Test
    void testGetServicesByDate_ShouldReturnServicesByDate() {
        // Given
        LocalDate testDate = LocalDate.now().plusDays(7);
        List<ServiceModel> expectedServices = Arrays.asList(testService);
        when(servicesUseCases.getAllServices()).thenReturn(expectedServices);
        
        // When
        List<ServiceModel> result = serviceService.getServicesByDate(testDate);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(servicesUseCases).getAllServices();
    }
    
    @Test
    void testGetServicesByDateRange_ShouldReturnServicesInRange() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        List<ServiceModel> expectedServices = Arrays.asList(testService);
        when(servicesUseCases.getAllServices()).thenReturn(expectedServices);
        
        // When
        List<ServiceModel> result = serviceService.getServicesByDateRange(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(servicesUseCases).getAllServices();
    }
    
    // Tests para validaciones de roles dinámicos
    
    @Test
    void testIsUserDirectorOfService_ShouldReturnTrue_WhenUserIsDirector() {
        // Given
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        
        // When
        boolean result = serviceService.isUserDirectorOfService("director1", "service1");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testIsUserDirectorOfService_ShouldReturnFalse_WhenUserIsNotDirector() {
        // Given
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        
        // When
        boolean result = serviceService.isUserDirectorOfService("musician1", "service1");
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testIsUserMusicianOfService_ShouldReturnTrue_WhenUserIsMusician() {
        // Given
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        
        // When
        boolean result = serviceService.isUserMusicianOfService("musician1", "service1");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testIsUserMusicianOfService_ShouldReturnFalse_WhenUserIsNotMusician() {
        // Given
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        
        // When
        boolean result = serviceService.isUserMusicianOfService("director1", "service1");
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testIsUserAdmin_ShouldReturnTrue_WhenUserIsAdmin() {
        // Given
        UserModel admin = new UserModel();
        admin.setId("admin1");
        admin.setRole(UserModel.Role.ADMIN);
        when(userUseCases.getUserById("admin1")).thenReturn(admin);
        
        // When
        boolean result = serviceService.isUserAdmin("admin1");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testDeleteExpiredServices_ShouldDeleteExpiredServices() {
        // Given
        ServiceModel expiredService = new ServiceModel();
        expiredService.setId("expired1");
        expiredService.setServiceDate(LocalDate.now().minusDays(1));
        
        List<ServiceModel> allServices = Arrays.asList(testService, expiredService);
        when(servicesUseCases.getAllServices()).thenReturn(allServices);
        
        // When
        serviceService.deleteExpiredServices();
        
        // Then
        verify(servicesUseCases).getAllServices();
        verify(servicesUseCases).deleteService("expired1");
    }
} 