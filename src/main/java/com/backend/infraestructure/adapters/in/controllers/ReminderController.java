package com.backend.infraestructure.adapters.in.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.application.IReminderService;
import com.backend.domain.model.ServiceModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Slf4j
public class ReminderController {
    
    private final IReminderService reminderService;
    
    /**
     * Endpoint para probar manualmente la búsqueda de servicios próximos
     * GET /api/reminders/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<ServiceModel>> getUpcomingServices() {
        try {
            List<ServiceModel> upcomingServices = reminderService.findUpcomingServices();
            log.info("🔍 Servicios próximos encontrados: {}", upcomingServices.size());
            return ResponseEntity.ok(upcomingServices);
        } catch (Exception e) {
            log.error("❌ Error obteniendo servicios próximos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint para probar manualmente el envío de recordatorios
     * POST /api/reminders/send
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendReminders() {
        try {
            log.info("🚀 Iniciando envío manual de recordatorios...");
            reminderService.sendReminderNotifications();
            return ResponseEntity.ok("Recordatorios enviados exitosamente");
        } catch (Exception e) {
            log.error("❌ Error enviando recordatorios: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para calcular días hasta el ensayo
     * GET /api/reminders/calculate-days?serviceDate=2025-08-25&practiceDate=2025-08-24
     */
    @GetMapping("/calculate-days")
    public ResponseEntity<Long> calculateDaysUntilPractice(
            String serviceDate, 
            String practiceDate) {
        try {
            if (serviceDate == null) {
                return ResponseEntity.badRequest().build();
            }
            
            java.time.LocalDate service = java.time.LocalDate.parse(serviceDate);
            java.time.LocalDate practice = practiceDate != null ? 
                java.time.LocalDate.parse(practiceDate) : null;
            
            long days = reminderService.calculateDaysUntilPractice(service, practice);
            return ResponseEntity.ok(days);
        } catch (Exception e) {
            log.error("❌ Error calculando días: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
