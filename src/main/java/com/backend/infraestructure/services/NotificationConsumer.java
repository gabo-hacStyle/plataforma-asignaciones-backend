package com.backend.infraestructure.services;

import com.backend.application.dto.NotificationMessage;
import com.backend.infraestructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    
    private final EmailService emailService;
    
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void processNotification(NotificationMessage notification) {
        try {
            log.info("📥 Procesando notificación de email: {}", notification.getUserEmail());
            
            // Enviar email
            emailService.sendNotificationEmail(notification);
            
            log.info("✅ Email procesado exitosamente: {}", notification.getUserEmail());
            
        } catch (Exception e) {
            log.error("❌ Error procesando email: {}", e.getMessage());
            // Aquí podrías implementar reintentos o dead letter queue
        }
    }
}
