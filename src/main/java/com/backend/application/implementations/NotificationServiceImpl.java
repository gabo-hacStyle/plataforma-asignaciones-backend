package com.backend.application.implementations;

import com.backend.application.INotificationService;
import com.backend.application.dto.NotificationMessage;
import com.backend.domain.port.UserUseCases;
import com.backend.application.dto.MusicianAssignment;
import com.backend.application.dto.UpdateAssingmentRequest;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;
import com.backend.infraestructure.services.NotificationProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {
    
    private final UserUseCases userUseCases;
    private final NotificationProducer notificationProducer;
    
    @Override
    public List<EmailNotificationBody> generateAssignmentNotifications(
            ServiceModel service, 
            UpdateAssingmentRequest.Assignments newAssignments, 
            UpdateAssingmentRequest.Assignments oldAssignments) {
        
        List<EmailNotificationBody> notifications = new ArrayList<>();
        List<NotificationMessage> queueMessages = new ArrayList<>();
        
        // Procesar directores asignados
        if (newAssignments.getDirectorIds() != null) {
            Set<String> oldDirectorIds = oldAssignments.getDirectorIds() != null ?
                    new HashSet<>(oldAssignments.getDirectorIds()) :
                Set.of();
            
            for (String directorId : newAssignments.getDirectorIds()) {
                if (!oldDirectorIds.contains(directorId)) {
                    // Nuevo director asignado
                    UserModel director = userUseCases.getUserById(directorId);
                    if (director != null) {
                        EmailNotificationBody notification = createDirectorAssignmentEmail(service, director);
                        notifications.add(notification);
                        
                        // Crear mensaje para la cola
                        NotificationMessage queueMessage = createNotificationMessage(notification, service, NotificationMessage.NotificationCategory.ASSIGNMENT);
                        queueMessages.add(queueMessage);
                    }
                }
            }
        }
        
        // Procesar músicos asignados
        if (newAssignments.getMusiciansList() != null) {
            Set<String> oldMusicianIds = oldAssignments.getMusiciansList() != null ? 
                oldAssignments.getMusiciansList().stream()
                    .flatMap(assignment -> assignment.getMusicianIds().stream())
                    .collect(Collectors.toSet()) : 
                Set.of();
            
            for (MusicianAssignment assignment : newAssignments.getMusiciansList()) {

               for (String musicianId : assignment.getMusicianIds()) {
                   if(!oldMusicianIds.contains(musicianId)) {
                       // Nuevo músico asignado
                       UserModel musician = userUseCases.getUserById(musicianId);
                       if (musician != null) {
                           EmailNotificationBody notification = createMusicianAssignmentEmail(service, musician, assignment.getInstrument());
                           notifications.add(notification);

                           // Crear mensaje para la cola
                           NotificationMessage queueMessage = createNotificationMessage(notification, service, NotificationMessage.NotificationCategory.ASSIGNMENT);
                           queueMessages.add(queueMessage);
                       }
                   }
               }
            }
        }
        
        // Enviar mensajes a la cola
        if (!queueMessages.isEmpty()) {
            notificationProducer.sendMultipleNotificationsToQueue(queueMessages);
        }
        
        return notifications;
    }
    
    @Override
    public List<EmailNotificationBody> generateRemovalNotifications(
            ServiceModel service, 
            UpdateAssingmentRequest.Assignments newAssignments, 
            UpdateAssingmentRequest.Assignments oldAssignments) {
        
        List<EmailNotificationBody> notifications = new ArrayList<>();
        List<NotificationMessage> queueMessages = new ArrayList<>();
        
        // Procesar directores removidos
        if (oldAssignments.getDirectorIds() != null) {
            Set<String> newDirectorIds = newAssignments.getDirectorIds() != null ? 
                newAssignments.getDirectorIds().stream().collect(Collectors.toSet()) : 
                Set.of();
            
            for (String directorId : oldAssignments.getDirectorIds()) {
                if (!newDirectorIds.contains(directorId)) {
                    // Director removido
                    UserModel director = userUseCases.getUserById(directorId);
                    if (director != null) {
                        EmailNotificationBody notification = createDirectorRemovalEmail(service, director);
                        notifications.add(notification);
                        
                        // Crear mensaje para la cola
                        NotificationMessage queueMessage = createNotificationMessage(notification, service, NotificationMessage.NotificationCategory.REMOVAL);
                        queueMessages.add(queueMessage);
                    }
                }
            }
        }
        
        // Procesar músicos removidos
        if (oldAssignments.getMusiciansList() != null) {
            Set<String> newMusicianIds = newAssignments.getMusiciansList() != null ? 
                newAssignments.getMusiciansList().stream()
                    .flatMap(assignment -> assignment.getMusicianIds().stream())
                    .collect(Collectors.toSet()) : 
                Set.of();
            
            for (MusicianAssignment assignment : oldAssignments.getMusiciansList()) {

                for (String musicianId : assignment.getMusicianIds()) {
                    if (!newMusicianIds.contains(musicianId)) {
                        // Músico removido
                        UserModel musician = userUseCases.getUserById(musicianId);
                        if (musician != null) {
                            EmailNotificationBody notification = createMusicianRemovalEmail(service, musician, assignment.getInstrument());
                            notifications.add(notification);

                            // Crear mensaje para la cola
                            NotificationMessage queueMessage = createNotificationMessage(notification, service, NotificationMessage.NotificationCategory.REMOVAL);
                            queueMessages.add(queueMessage);
                        }
                    }
                }
            }
        }
        
        // Enviar mensajes a la cola
        if (!queueMessages.isEmpty()) {
            notificationProducer.sendMultipleNotificationsToQueue(queueMessages);
        }
        
        return notifications;
    }
    
    /**
     * Convierte EmailNotificationBody a NotificationMessage para la cola
     */
    private NotificationMessage createNotificationMessage(EmailNotificationBody emailNotification, ServiceModel service, NotificationMessage.NotificationCategory category) {
        return new NotificationMessage(
            NotificationMessage.NotificationType.EMAIL,
            category,
            emailNotification.getUserName(),
            emailNotification.getUserEmail(),
            emailNotification.getUserRole(),
            emailNotification.getInstrument(),
            emailNotification.getSubject(),
            emailNotification.getEmailBody(),
            service.getId(),
            formatDate(service.getServiceDate()),
            service.getLocation(),
            formatDate(service.getPracticeDate())
        );
    }
    
    /**
     * Crea el email para un director asignado
     */
    private EmailNotificationBody createDirectorAssignmentEmail(ServiceModel service, UserModel director) {
        String subject = "Has sido asignado como Director - Servicio del " + formatDate(service.getServiceDate());
        
        // El contenido del email ahora se genera desde el template de Thymeleaf
        String emailBody = "Contenido generado desde template";
        
        return new EmailNotificationBody(
            director.getName(),
            director.getEmail(),
            "DIRECTOR",
            null,
            emailBody,
            subject
        );
    }
    
    /**
     * Crea el email para un músico asignado
     */
    private EmailNotificationBody createMusicianAssignmentEmail(ServiceModel service, UserModel musician, String instrument) {
        String subject = "Has sido asignado como Músico - Servicio del " + formatDate(service.getServiceDate());
        
        // El contenido del email ahora se genera desde el template de Thymeleaf
        String emailBody = "Contenido generado desde template";
        
        return new EmailNotificationBody(
            musician.getName(),
            musician.getEmail(),
            "MUSICIAN",
            instrument,
            emailBody,
            subject
        );
    }
    
    /**
     * Crea el email para un director removido
     */
    private EmailNotificationBody createDirectorRemovalEmail(ServiceModel service, UserModel director) {
        String subject = "Cambio en tu asignación - Servicio del " + formatDate(service.getServiceDate());
        
        // El contenido del email ahora se genera desde el template de Thymeleaf
        String emailBody = "Contenido generado desde template";
        
        return new EmailNotificationBody(
            director.getName(),
            director.getEmail(),
            "DIRECTOR",
            null,
            emailBody,
            subject
        );
    }
    
    /**
     * Crea el email para un músico removido
     */
    private EmailNotificationBody createMusicianRemovalEmail(ServiceModel service, UserModel musician, String instrument) {
        String subject = "Cambio en tu asignación - Servicio del " + formatDate(service.getServiceDate());
        
        // El contenido del email ahora se genera desde el template de Thymeleaf
        String emailBody = "Contenido generado desde template";
        
        return new EmailNotificationBody(
            musician.getName(),
            musician.getEmail(),
            "MUSICIAN",
            instrument,
            emailBody,
            subject
        );
    }
    
    /**
     * Formatea una fecha para mostrar en los emails
     */
    private String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return "Fecha por confirmar";
        }
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    @Override
    public void generateReminderNotification(NotificationMessage notificationMessage) {
        try {
            // Enviar la notificación de recordatorio directamente a la cola
            notificationProducer.sendNotificationToQueue(notificationMessage);
            
            log.info("📧 Notificación de recordatorio enviada a la cola para: {}", 
                notificationMessage.getUserEmail());
                
        } catch (Exception e) {
            log.error("❌ Error enviando notificación de recordatorio: {}", e.getMessage());
        }
    }
}
