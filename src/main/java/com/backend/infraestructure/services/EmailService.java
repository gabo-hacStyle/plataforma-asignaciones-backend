package com.backend.infraestructure.services;

import com.backend.application.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    public void sendNotificationEmail(NotificationMessage notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(notification.getUserEmail());
            helper.setSubject(notification.getSubject());
            
            // Usar template de Thymeleaf
            String htmlContent = generateEmailContent(notification);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("✅ Email enviado exitosamente a: {}", notification.getUserEmail());
            
        } catch (MessagingException e) {
            log.error("❌ Error enviando email a {}: {}", notification.getUserEmail(), e.getMessage());
        }
    }
    
    private String generateEmailContent(NotificationMessage notification) {
        Context context = new Context();
        
        // Datos para el template
        context.setVariable("userName", notification.getUserId());
        context.setVariable("userRole", notification.getUserRole());
        context.setVariable("instrument", notification.getInstrument());
        context.setVariable("serviceDate", notification.getServiceDate());
        context.setVariable("serviceLocation", notification.getServiceLocation());
        context.setVariable("practiceDate", notification.getPracticeDate());
        context.setVariable("category", notification.getCategory().name());
        context.setVariable("serviceId", notification.getServiceId());
        
        // Variables adicionales para recordatorios
        if (notification.getCategory() == NotificationMessage.NotificationCategory.REMINDER) {
            // Calcular días hasta el ensayo
            try {
                if (notification.getPracticeDate() != null && !notification.getPracticeDate().equals("Por confirmar")) {
                    java.time.LocalDate practiceDate = java.time.LocalDate.parse(notification.getPracticeDate(), 
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    java.time.LocalDate today = java.time.LocalDate.now();
                    long daysUntilPractice = java.time.temporal.ChronoUnit.DAYS.between(today, practiceDate);
                    context.setVariable("daysUntilPractice", daysUntilPractice > 0 ? daysUntilPractice : 0);
                } else {
                    context.setVariable("daysUntilPractice", 0);
                }
            } catch (Exception e) {
                context.setVariable("daysUntilPractice", 0);
            }
        }
        
        // Seleccionar template según el tipo de notificación
        String templateName = getTemplateName(notification);
        
        return templateEngine.process(templateName, context);
    }
    
    private String getTemplateName(NotificationMessage notification) {
        if (notification.getCategory() == NotificationMessage.NotificationCategory.ASSIGNMENT) {
            return notification.getUserRole().equals("DIRECTOR") ? 
                "director-assignment" : "musician-assignment";
        } else if (notification.getCategory() == NotificationMessage.NotificationCategory.REMOVAL) {
            return notification.getUserRole().equals("DIRECTOR") ? 
                "director-removal" : "musician-removal";
        } else if (notification.getCategory() == NotificationMessage.NotificationCategory.REMINDER) {
            return notification.getUserRole().equals("DIRECTOR") ? 
                "director-reminder" : "musician-reminder";
        } else {
            // Fallback para casos no manejados
            return "musician-assignment";
        }
    }
}
