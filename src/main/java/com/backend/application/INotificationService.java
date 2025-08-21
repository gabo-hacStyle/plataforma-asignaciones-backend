package com.backend.application;

import com.backend.application.dto.UpdateAssingmentRequest;
import com.backend.domain.model.ServiceModel;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public interface INotificationService {
    
    /**
     * Genera el cuerpo del email para usuarios asignados a un servicio
     * @param service Servicio al que fueron asignados
     * @param newAssignments Asignaciones nuevas
     * @param oldAssignments Asignaciones anteriores
     * @return Lista de cuerpos de email para cada usuario asignado
     */
    List<EmailNotificationBody> generateAssignmentNotifications(
        ServiceModel service, 
        UpdateAssingmentRequest.Assignments newAssignments, 
        UpdateAssingmentRequest.Assignments oldAssignments
    );
    
    /**
     * Genera el cuerpo del email para usuarios removidos de un servicio
     * @param service Servicio del que fueron removidos
     * @param newAssignments Asignaciones nuevas
     * @param oldAssignments Asignaciones anteriores
     * @return Lista de cuerpos de email para cada usuario removido
     */
    List<EmailNotificationBody> generateRemovalNotifications(
        ServiceModel service, 
        UpdateAssingmentRequest.Assignments newAssignments, 
        UpdateAssingmentRequest.Assignments oldAssignments
    );
    
    /**
     * Clase interna para representar el cuerpo de un email de notificación
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class EmailNotificationBody {
        private String userName;
        private String userEmail;
        private String userRole; // "DIRECTOR" o "MUSICIAN"
        private String instrument; // Solo para músicos
        private String emailBody;
        private String subject;
        
    }
}
