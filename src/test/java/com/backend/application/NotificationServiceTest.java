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

import com.backend.application.dto.MusicianAssignment;
import com.backend.application.dto.UpdateAssingmentRequest;
import com.backend.application.implementations.NotificationServiceImpl;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;
import com.backend.domain.port.UserUseCases;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserUseCases userUseCases;
    
    @InjectMocks
    private NotificationServiceImpl notificationService;
    
    private ServiceModel testService;
    private UserModel testDirector;
    private UserModel testMusician;
    private UpdateAssingmentRequest.Assignments oldAssignments;
    private UpdateAssingmentRequest.Assignments newAssignments;
    
    @BeforeEach
    void setUp() {
        // Crear servicio de prueba
        testService = new ServiceModel();
        testService.setId("service1");
        testService.setServiceDate(LocalDate.of(2024, 2, 15));
        testService.setPracticeDate(LocalDate.of(2024, 2, 13));
        testService.setLocation("Iglesia Principal");
        
        // Crear director de prueba
        testDirector = new UserModel();
        testDirector.setId("director1");
        testDirector.setName("Juan Director");
        testDirector.setEmail("juan@iglesia.com");
        testDirector.setRoles(Arrays.asList(UserModel.Role.DIRECTOR));
        
        // Crear músico de prueba
        testMusician = new UserModel();
        testMusician.setId("musician1");
        testMusician.setName("María Músico");
        testMusician.setEmail("maria@iglesia.com");
        testMusician.setRoles(Arrays.asList(UserModel.Role.MUSICIAN));
        
        // Configurar asignaciones antiguas
        oldAssignments = new UpdateAssingmentRequest.Assignments();
        oldAssignments.setDirectorIds(Arrays.asList("director1"));
        oldAssignments.setMusiciansList(Arrays.asList(
            new MusicianAssignment("musician1", "Piano")
        ));
        
        // Configurar asignaciones nuevas
        newAssignments = new UpdateAssingmentRequest.Assignments();
        newAssignments.setDirectorIds(Arrays.asList("director1", "director2"));
        newAssignments.setMusiciansList(Arrays.asList(
            new MusicianAssignment("musician1", "Piano"),
            new MusicianAssignment("musician2", "Guitarra")
        ));
    }
    
    @Test
    void testGenerateAssignmentNotifications_ShouldGenerateNotificationsForNewAssignments() {
        // Arrange
        UserModel newDirector = new UserModel();
        newDirector.setId("director2");
        newDirector.setName("Pedro Director");
        newDirector.setEmail("pedro@iglesia.com");
        newDirector.setRoles(Arrays.asList(UserModel.Role.DIRECTOR));
        
        UserModel newMusician = new UserModel();
        newMusician.setId("musician2");
        newMusician.setName("Carlos Músico");
        newMusician.setEmail("carlos@iglesia.com");
        newMusician.setRoles(Arrays.asList(UserModel.Role.MUSICIAN));
        
        when(userUseCases.getUserById("director2")).thenReturn(newDirector);
        when(userUseCases.getUserById("musician2")).thenReturn(newMusician);
        
        // Act
        List<INotificationService.EmailNotificationBody> notifications = 
            notificationService.generateAssignmentNotifications(testService, newAssignments, oldAssignments);
        
        // Assert
        assertEquals(2, notifications.size());
        
        // Verificar notificación del nuevo director
        INotificationService.EmailNotificationBody directorNotification = notifications.stream()
            .filter(n -> n.getUserRole().equals("DIRECTOR"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(directorNotification);
        assertEquals("pedro@iglesia.com", directorNotification.getUserEmail());
        assertEquals("Pedro Director", directorNotification.getUserName());
        assertTrue(directorNotification.getSubject().contains("Has sido asignado como Director"));
        assertTrue(directorNotification.getEmailBody().contains("DIRECTOR"));
        assertTrue(directorNotification.getEmailBody().contains("15/02/2024"));
        
        // Verificar notificación del nuevo músico
        INotificationService.EmailNotificationBody musicianNotification = notifications.stream()
            .filter(n -> n.getUserRole().equals("MUSICIAN"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(musicianNotification);
        assertEquals("carlos@iglesia.com", musicianNotification.getUserEmail());
        assertEquals("Carlos Músico", musicianNotification.getUserName());
        assertEquals("Guitarra", musicianNotification.getInstrument());
        assertTrue(musicianNotification.getSubject().contains("Has sido asignado como Músico"));
        assertTrue(musicianNotification.getEmailBody().contains("MÚSICO"));
        assertTrue(musicianNotification.getEmailBody().contains("Guitarra"));
    }
    
    @Test
    void testGenerateRemovalNotifications_ShouldGenerateNotificationsForRemovedAssignments() {
        // Arrange
        UpdateAssingmentRequest.Assignments oldAssignmentsWithMore = new UpdateAssingmentRequest.Assignments();
        oldAssignmentsWithMore.setDirectorIds(Arrays.asList("director1", "director3"));
        oldAssignmentsWithMore.setMusiciansList(Arrays.asList(
            new MusicianAssignment("musician1", "Piano"),
            new MusicianAssignment("musician3", "Bajo")
        ));
        
        UserModel removedDirector = new UserModel();
        removedDirector.setId("director3");
        removedDirector.setName("Ana Director");
        removedDirector.setEmail("ana@iglesia.com");
        removedDirector.setRoles(Arrays.asList(UserModel.Role.DIRECTOR));
        
        UserModel removedMusician = new UserModel();
        removedMusician.setId("musician3");
        removedMusician.setName("Luis Músico");
        removedMusician.setEmail("luis@iglesia.com");
        removedMusician.setRoles(Arrays.asList(UserModel.Role.MUSICIAN));
        
        when(userUseCases.getUserById("director3")).thenReturn(removedDirector);
        when(userUseCases.getUserById("musician3")).thenReturn(removedMusician);
        
        // Act
        List<INotificationService.EmailNotificationBody> notifications = 
            notificationService.generateRemovalNotifications(testService, newAssignments, oldAssignmentsWithMore);
        
        // Assert
        assertEquals(2, notifications.size());
        
        // Verificar notificación del director removido
        INotificationService.EmailNotificationBody directorNotification = notifications.stream()
            .filter(n -> n.getUserRole().equals("DIRECTOR"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(directorNotification);
        assertEquals("ana@iglesia.com", directorNotification.getUserEmail());
        assertEquals("Ana Director", directorNotification.getUserName());
        assertTrue(directorNotification.getSubject().contains("Cambio en tu asignación"));
        assertTrue(directorNotification.getEmailBody().contains("ya no estás asignado como DIRECTOR"));
        
        // Verificar notificación del músico removido
        INotificationService.EmailNotificationBody musicianNotification = notifications.stream()
            .filter(n -> n.getUserRole().equals("MUSICIAN"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(musicianNotification);
        assertEquals("luis@iglesia.com", musicianNotification.getUserEmail());
        assertEquals("Luis Músico", musicianNotification.getUserName());
        assertEquals("Bajo", musicianNotification.getInstrument());
        assertTrue(musicianNotification.getSubject().contains("Cambio en tu asignación"));
        assertTrue(musicianNotification.getEmailBody().contains("ya no estás asignado como MÚSICO"));
        assertTrue(musicianNotification.getEmailBody().contains("Bajo"));
    }
    
    @Test
    void testGenerateAssignmentNotifications_ShouldReturnEmptyList_WhenNoNewAssignments() {
        // Act
        List<INotificationService.EmailNotificationBody> notifications = 
            notificationService.generateAssignmentNotifications(testService, oldAssignments, oldAssignments);
        
        // Assert
        assertTrue(notifications.isEmpty());
    }
    
    @Test
    void testGenerateRemovalNotifications_ShouldReturnEmptyList_WhenNoRemovals() {
        // Act
        List<INotificationService.EmailNotificationBody> notifications = 
            notificationService.generateRemovalNotifications(testService, newAssignments, newAssignments);
        
        // Assert
        assertTrue(notifications.isEmpty());
    }
    
    @Test
    void testGenerateAssignmentNotifications_ShouldHandleNullAssignments() {
        // Arrange
        UpdateAssingmentRequest.Assignments nullOldAssignments = new UpdateAssingmentRequest.Assignments();
        nullOldAssignments.setDirectorIds(null);
        nullOldAssignments.setMusiciansList(null);
        
        when(userUseCases.getUserById("director1")).thenReturn(testDirector);
        when(userUseCases.getUserById("musician1")).thenReturn(testMusician);
        
        // Act
        List<INotificationService.EmailNotificationBody> notifications = 
            notificationService.generateAssignmentNotifications(testService, newAssignments, nullOldAssignments);
        
        // Assert
        assertEquals(2, notifications.size()); // Ambos director1 y musician1 son nuevos
    }
}
