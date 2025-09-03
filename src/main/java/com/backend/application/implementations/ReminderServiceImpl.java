package com.backend.application.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.backend.application.INotificationService;
import com.backend.application.IReminderService;
import com.backend.application.dto.NotificationMessage.NotificationCategory;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.UserModel;
import com.backend.domain.port.ServicesUseCases;
import com.backend.domain.port.UserUseCases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderServiceImpl implements IReminderService {
    
    private final ServicesUseCases servicesUseCases;
    private final UserUseCases userUseCases;
    private final INotificationService notificationService;
    
    @Override
    public List<ServiceModel> findUpcomingServices() {
        LocalDate today = LocalDate.now();
        LocalDate tenDaysFromNow = today.plusDays(10);
        
        List<ServiceModel> allServices = servicesUseCases.getAllServices();
        
        return allServices.stream()
                .filter(service -> service.getServiceDate() != null)
                .filter(service -> !service.getServiceDate().isBefore(today))
                .filter(service -> !service.getServiceDate().isAfter(tenDaysFromNow))
                .collect(Collectors.toList());
    }
    
    @Override
    public long calculateDaysUntilPractice(LocalDate serviceDate, LocalDate practiceDate) {
        if (practiceDate == null) {
            // Si no hay fecha de ensayo, calcular días hasta el servicio
            return ChronoUnit.DAYS.between(LocalDate.now(), serviceDate);
        }
        
        LocalDate today = LocalDate.now();
        if (practiceDate.isBefore(today)) {
            // Si el ensayo ya pasó, retornar 0
            return 0;
        }
        
        return ChronoUnit.DAYS.between(today, practiceDate);
    }
    
    @Override
    @Scheduled(cron = "0 0 9 * * SUN,WED") // Cada domingo y miércoles a las 9:00 AM
    public void sendReminderNotifications() {
        try {
            log.info("🕐 Iniciando envío de recordatorios automáticos...");
            
            List<ServiceModel> upcomingServices = findUpcomingServices();
            
            if (upcomingServices.isEmpty()) {
                log.info("📅 No hay servicios próximos en los próximos 10 días");
                return;
            }
            
            log.info("📧 Enviando recordatorios para {} servicios próximos", upcomingServices.size());
            
            for (ServiceModel service : upcomingServices) {
                sendRemindersForService(service);
            }
            
            log.info("✅ Recordatorios enviados exitosamente para {} servicios", upcomingServices.size());
            
        } catch (Exception e) {
            log.error("❌ Error enviando recordatorios automáticos: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Envía recordatorios para un servicio específico
     */
    private void sendRemindersForService(ServiceModel service) {
        try {
            // Enviar recordatorios a directores
            if (service.getDirectors() != null && !service.getDirectors().isEmpty()) {
                for (UserModel director : service.getDirectors()) {
                    sendDirectorReminder(director, service);
                }
            }
            
            // Enviar recordatorios a músicos
            if (service.getMusiciansList() != null && !service.getMusiciansList().isEmpty()) {
                for (var musicianAssignment : service.getMusiciansList()) {
                    sendMusicianReminder(musicianAssignment.getMusician(), 
                                       musicianAssignment.getInstrument(), service);
                }
            }
            
        } catch (Exception e) {
            log.error("❌ Error enviando recordatorios para servicio {}: {}", 
                service.getId(), e.getMessage());
        }
    }
    
    /**
     * Envía recordatorio a un director
     */
    private void sendDirectorReminder(UserModel director, ServiceModel service) {
        try {
            long daysUntilPractice = calculateDaysUntilPractice(
                service.getServiceDate(), service.getPracticeDate());
            
            String subject = "🎵 Recordatorio de Servicio - Verificar Canciones";
            
            
            // Crear notificación para el director
            createReminderNotification(director, subject, service, "DIRECTOR");
            
            log.info("📧 Recordatorio enviado al director {} para servicio {}", 
                director.getName(), service.getId());
            
        } catch (Exception e) {
            log.error("❌ Error enviando recordatorio al director {}: {}", 
                director.getName(), e.getMessage());
        }
    }
    
    /**
     * Envía recordatorio a un músico
     */
    private void sendMusicianReminder(UserModel musician, String instrument, ServiceModel service) {
        try {
            long daysUntilPractice = calculateDaysUntilPractice(
                service.getServiceDate(), service.getPracticeDate());
            
            String subject = "🎵 Recordatorio de Ensayo - Preparar Instrumento";
           
            // Crear notificación para el músico
            createReminderNotification(musician, subject, service, "MUSICIAN");
            
            log.info("📧 Recordatorio enviado al músico {} para servicio {}", 
                musician.getName(), service.getId());
            
        } catch (Exception e) {
            log.error("❌ Error enviando recordatorio al músico {}: {}", 
                musician.getName(), e.getMessage());
        }
    }
    
    /**
     * Crea y envía la notificación de recordatorio
     */
    private void createReminderNotification(UserModel user, String subject, 
                                         ServiceModel service, String role) {
        try {
            // Crear mensaje de notificación
            var notificationMessage = new com.backend.application.dto.NotificationMessage();
            notificationMessage.setUserId(user.getName());
            notificationMessage.setUserEmail(user.getEmail());
            notificationMessage.setUserRole(role);
            notificationMessage.setSubject(subject);
            notificationMessage.setServiceDate(formatDate(service.getServiceDate()));
            notificationMessage.setServiceLocation(service.getLocation());
            notificationMessage.setPracticeDate(service.getPracticeDate() != null ? 
                formatDate(service.getPracticeDate()) : null);
            notificationMessage.setCategory(NotificationCategory.REMINDER);
            
            // Enviar notificación a través del sistema existente
            notificationService.generateReminderNotification(notificationMessage);
            
        } catch (Exception e) {
            log.error("❌ Error creando notificación de recordatorio: {}", e.getMessage());
        }
    }
    
    /**
     * Formatea una fecha para mostrar en los mensajes
     */
    private String formatDate(LocalDate date) {
        if (date == null) return "Por confirmar";
        
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
