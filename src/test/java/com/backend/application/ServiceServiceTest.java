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

import com.backend.application.dto.CreateSongListRequest;
import com.backend.application.dto.MusicianAssignment;
import com.backend.application.dto.UpdateAssingmentRequest;
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
        assertEquals(testMusician.getId(), result.get(0).getId());
        verify(userUseCases).getAllUsers();
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
        musicianAsDirector.setId("musician1");
        musicianAsDirector.setRole(UserModel.Role.MUSICIAN);
        
        List<String> directorIds = Arrays.asList("musician1");
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(userUseCases.getUserById("musician1")).thenReturn(musicianAsDirector);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.assignDirectorsToService("service1", directorIds);
        
        // Then
        assertNotNull(result);
        verify(userUseCases).updateUser(musicianAsDirector);
        assertEquals(UserModel.Role.DIRECTOR, musicianAsDirector.getRole());
    }
    
    @Test
    void testAssignMusiciansToService_ShouldAssignMusicians() {
        // Given
        List<MusicianAssignment> musicianAssignments = Arrays.asList(
            new MusicianAssignment("musician1", "Piano"),
            new MusicianAssignment("musician2", "Guitarra")
        );
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        when(userUseCases.getUserById("musician2")).thenReturn(testMusician);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.assignMusiciansToService("service1", musicianAssignments);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases).updateService(any(ServiceModel.class));
    }
    
    @Test
    void testUpdateServiceAssignments_ShouldUpdateAssignments() {
        // Given
        UpdateAssingmentRequest request = new UpdateAssingmentRequest();
        request.setDirectorIds(Arrays.asList("director1"));
        request.setMusiciansList(Arrays.asList(new MusicianAssignment("musician1", "Piano")));
        
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(userUseCases.getUserById("director1")).thenReturn(testDirector);
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.updateServiceAssignments("service1", request);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases, times(2)).updateService(any(ServiceModel.class));
    }
    
    // Tests para historias de usuario del Director
    
    @Test
    void testCreateSongListForService_ShouldCreateSongList() {
        // Given
        List<CreateSongListRequest> songs = Arrays.asList(
            new CreateSongListRequest("Agnus dei", "Su presencia", "youtube.com/1", "0"),
            new CreateSongListRequest("Holy forever", "Montesanto", "youtube.com/2", "-1")
        );
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.createSongListForService("service1", "director1", songs);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases).updateService(any(ServiceModel.class));
    }
    
    @Test
    void testCreateSongListForService_ShouldThrowException_WhenNotDirector() {
        // Given
        List<CreateSongListRequest> songs = Arrays.asList(
            new CreateSongListRequest("Agnus dei", "Su presencia", "youtube.com/1", "0")
        );
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceService.createSongListForService("service1", "musician1", songs);
        });
    }
    
    @Test
    void testUpdateSongListForService_ShouldUpdateSongList() {
        // Given
        List<CreateSongListRequest> songs = Arrays.asList(
            new CreateSongListRequest("Canción Actualizada", "Compositor Actualizado", "https://youtube.com/updated", "Original")
        );
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.updateSongListForService("service1", "director1", songs);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases).updateService(any(ServiceModel.class));
    }
    
    @Test
    void testUpdateService_ShouldUpdateService() {
        // Given
        ServiceModel serviceUpdate = new ServiceModel();
        serviceUpdate.setId("service1");
        serviceUpdate.setLocation("Nueva Ubicación");
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.updateService(serviceUpdate);
        
        // Then
        assertNotNull(result);
        verify(servicesUseCases).updateService(any(ServiceModel.class));
    }
    
    @Test
    void testUpdateService_ShouldUpdatePartialService() {
        // Given
        ServiceModel partialUpdate = new ServiceModel();
        partialUpdate.setId("service1");
        partialUpdate.setServiceDate(LocalDate.now().plusDays(10));
        // Only service date specified
        
        when(servicesUseCases.getServiceById("service1")).thenReturn(testService);
        when(servicesUseCases.updateService(any(ServiceModel.class))).thenReturn(testService);
        
        // When
        ServiceModel result = serviceService.updateService(partialUpdate);
        
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