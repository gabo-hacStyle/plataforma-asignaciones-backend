package com.backend.infraestructure.services;

import com.backend.application.dto.NotificationMessage;
import com.backend.infraestructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void sendNotificationToQueue(NotificationMessage notification) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                notification
            );
            
            log.info("📤 Notificación enviada a la cola: {} - {}", 
                notification.getType(), notification.getUserEmail());
                
        } catch (Exception e) {
            log.error("❌ Error enviando notificación a la cola: {}", e.getMessage());
        }
    }
    
    public void sendMultipleNotificationsToQueue(List<NotificationMessage> notifications) {
        for (NotificationMessage notification : notifications) {
            sendNotificationToQueue(notification);
        }
        
        log.info("📤 {} notificaciones enviadas a la cola", notifications.size());
    }
}
