package com.backend.application;

import java.util.List;
import com.backend.domain.model.ServiceModel;

public interface IReminderService {
    
    /**
     * Busca servicios dentro de los próximos 10 días
     * @return Lista de servicios próximos
     */
    List<ServiceModel> findUpcomingServices();
    
    /**
     * Envía recordatorios a directores y músicos de servicios próximos
     * Se ejecuta automáticamente los domingos y miércoles
     */
    void sendReminderNotifications();
    
    /**
     * Calcula los días restantes hasta el ensayo
     * @param serviceDate Fecha del servicio
     * @param practiceDate Fecha del ensayo
     * @return Días restantes hasta el ensayo
     */
    long calculateDaysUntilPractice(java.time.LocalDate serviceDate, java.time.LocalDate practiceDate);
}
