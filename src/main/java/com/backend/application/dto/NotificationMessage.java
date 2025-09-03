package com.backend.application.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
    
    public enum NotificationType {
        EMAIL
    }
    
    public enum NotificationCategory {
        ASSIGNMENT,
        REMOVAL,
        REMINDER
    }
    
    private NotificationType type;
    private NotificationCategory category;
    private String userId;
    private String userEmail;
    private String userRole;
    private String instrument; // Solo para músicos
    private String subject;
    private String emailBody;
    private String serviceId;
    private String serviceDate;
    private String serviceLocation;
    private String practiceDate;
}
