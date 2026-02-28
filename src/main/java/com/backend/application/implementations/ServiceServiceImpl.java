package com.backend.application.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.backend.application.dto.*;
import org.springframework.stereotype.Service;
import com.backend.application.INotificationService;
import com.backend.application.IServiceService;
import com.backend.domain.port.UserUseCases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.domain.model.MusiciansList;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.SongsModel;
import com.backend.domain.model.UserModel;
import com.backend.domain.port.ServicesUseCases;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceServiceImpl implements IServiceService {
    
    private final ServicesUseCases servicesUseCases;
    
    
    private final UserUseCases userUseCases;
    
    private final INotificationService notificationService;
    
    // Historias de usuario del Admin
    @Override
    public ServiceModel createService(ServiceModel service) {
        if (service == null) {
            throw new IllegalArgumentException("El servicio no puede ser null");
        }
        
        // Validar fechas
        validateServiceDates(service);
        
        // Establecer fecha de creación si no existe
        if (service.getCreatedAt() == null) {
            service.setCreatedAt(LocalDateTime.now());
        }
        
        // Inicializar listas si son null
        initializeServiceLists(service);
        
        return servicesUseCases.createService(service);
    }
    
    @Override
    @Transactional
    public ServiceModel createServiceWithAssignments(LocalDate serviceDate, LocalDate practiceDate, String location,
                                                   List<String> directorIds, List<MusicianAssignment> musicianAssignments) {
        try{
            // Validar parámetros básicos
            if (serviceDate == null) {
                throw new IllegalArgumentException("La fecha del servicio no puede ser null");
            }
            if (location == null || location.trim().isEmpty()) {
                throw new IllegalArgumentException("La ubicación no puede estar vacía");
            }

            // Crear el servicio base
            ServiceModel service = new ServiceModel();
            service.setServiceDate(serviceDate);
            service.setPracticeDate(practiceDate);
            service.setLocation(location);
            service.setCreatedAt(LocalDateTime.now());

            // Asignar directores
            if (directorIds != null && !directorIds.isEmpty()) {
                List<UserModel> directors = new ArrayList<>();
                for (String directorId : directorIds) {
                    UserModel director = userUseCases.getUserById(directorId);
                    if (director == null) {
                        throw new IllegalArgumentException("Director no encontrado: " + directorId);
                    }

                    // Añadir rol DIRECTOR si no lo tiene ya
                    if (director.getRoles() == null) {
                        director.setRoles(new ArrayList<>());
                    }
                    if (!director.getRoles().contains(UserModel.Role.DIRECTOR)) {
                        director.getRoles().add(UserModel.Role.DIRECTOR);
                        userUseCases.updateUser(director);
                    }

                    directors.add(director);
                }
                service.setDirectors(directors);
            }

            // Asignar músicos con instrumentos
            if (musicianAssignments != null && !musicianAssignments.isEmpty()) {
                List<MusiciansList> musiciansList = new ArrayList<>();
                for (MusicianAssignment assignment : musicianAssignments) {
                    List<UserModel> musicians = assignment.getMusicianIds().stream()
                            .map(id -> {
                                UserModel user = userUseCases.getUserById(id);
                                if (user == null) {
                                    throw new IllegalArgumentException("No se encontró el músico con id: " + id);
                                }
                                return user;
                            })
                            .toList();


                    MusiciansList musicianAssignment = new MusiciansList();
                    musicianAssignment.setMusician(musicians);
                    musicianAssignment.setInstrument(assignment.getInstrument());
                    musiciansList.add(musicianAssignment);
                }
                service.setMusiciansList(musiciansList);
            }

            // Crear el servicio en la base de datos
            ServiceModel createdService = servicesUseCases.createService(service);

            // Generar notificaciones de asignación para la creación del servicio
            generateCreationNotifications(createdService, directorIds, musicianAssignments);

            return createdService;
        } catch (Exception e){
            log.error("❌ Error creando servicio con asignaciones: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public OCRResponseDTO createServiceWithOCR(List<OCRRequestInfoDTO> requests) {
        return null;
    }


    @Override
    public List<UserModel> getAllMusicians() {
        List<UserModel> allUsers = userUseCases.getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(UserModel.Role.MUSICIAN))
                .collect(Collectors.toList());
    }
    
    @Override
    public ServiceModel assignDirectorsToService(String serviceId, List<String> directorIds) {
        validateServiceExists(serviceId);
        validateDirectorIds(directorIds);
        
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        List<UserModel> directors = new ArrayList<>();
        
        for (String directorId : directorIds) {
            UserModel director = userUseCases.getUserById(directorId);
            if (director == null) {
                throw new IllegalArgumentException("Director no encontrado: " + directorId);
            }
            
            // Añadir rol DIRECTOR si no lo tiene ya
            if (director.getRoles() == null) {
                director.setRoles(new ArrayList<>());
            }
            if (!director.getRoles().contains(UserModel.Role.DIRECTOR)) {
                director.getRoles().add(UserModel.Role.DIRECTOR);
                userUseCases.updateUser(director);
            }
            
            directors.add(director);
        }
        
        service.setDirectors(directors);
        return servicesUseCases.updateService(service);
    }
    
        @Override
        @Transactional
        public ServiceModel assignMusiciansToService(String serviceId, List<MusicianAssignment> musicianAssignments) {
        validateServiceExists(serviceId);
       
        
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        List<MusiciansList> musiciansList = new ArrayList<>();
        
        for (MusicianAssignment assignment : musicianAssignments) {
            List<UserModel> musicians = assignment.getMusicianIds().stream()
                    .map(id -> {
                        UserModel user = userUseCases.getUserById(id);
                        if (user == null) {
                            throw new IllegalArgumentException("No se encontró el músico con id: " + id);
                        }
                        return user;
                    })
                    .toList();

            MusiciansList musicianAssignment = new MusiciansList();
            musicianAssignment.setMusician(musicians);
            musicianAssignment.setInstrument(assignment.getInstrument());
            musiciansList.add(musicianAssignment);
        }
        
        service.setMusiciansList(musiciansList);
        return servicesUseCases.updateService(service);
    }
    
    @Override
    public ServiceModel updateServiceAssignments(String serviceId, UpdateAssingmentRequest request) {
        validateServiceExists(serviceId);
        
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        
        // Usar las asignaciones que vienen del frontend
        UpdateAssingmentRequest.Assignments oldAssignments = request.getOldAssignments();
        UpdateAssingmentRequest.Assignments newAssignments = request.getNewAssignments();
        
        // Actualizar directores
        if (newAssignments.getDirectorIds() != null && !newAssignments.getDirectorIds().isEmpty()) {
            service = assignDirectorsToService(serviceId, newAssignments.getDirectorIds());
        }
        
        // Actualizar músicos
        if (newAssignments.getMusiciansList() != null && !newAssignments.getMusiciansList().isEmpty()) {
            service = assignMusiciansToService(serviceId, newAssignments.getMusiciansList());
        }
        
        // Generar notificaciones para usuarios asignados
        List<INotificationService.EmailNotificationBody> assignmentNotifications = 
            notificationService.generateAssignmentNotifications(service, newAssignments, oldAssignments);
        
        // Generar notificaciones para usuarios removidos
        List<INotificationService.EmailNotificationBody> removalNotifications = 
            notificationService.generateRemovalNotifications(service, newAssignments, oldAssignments);
        
        // Las notificaciones se envían automáticamente a través de la cola
        log.info("📧 {} notificaciones de asignación y {} de remoción enviadas a la cola", 
            assignmentNotifications.size(), removalNotifications.size());
        
        return service;
    }
    
    @Override
    public ServiceModel updateService(ServiceModel service) {
        validateServiceExists(service.getId());
        
        // Obtener el servicio existente
        ServiceModel existingService = servicesUseCases.getServiceById(service.getId());
        
        // Actualizar solo los campos que no son null
        if (service.getServiceDate() != null) {
            existingService.setServiceDate(service.getServiceDate());
        }
        if (service.getPracticeDate() != null) {
            existingService.setPracticeDate(service.getPracticeDate());
        }
        if (service.getLocation() != null && !service.getLocation().trim().isEmpty()) {
            existingService.setLocation(service.getLocation());
        }
        if (service.getDirectors() != null) {
            existingService.setDirectors(service.getDirectors());
        }
        if (service.getMusiciansList() != null) {
            existingService.setMusiciansList(service.getMusiciansList());
        }
        if (service.getSongsList() != null) {
            existingService.setSongsList(service.getSongsList());
        }
        
        return servicesUseCases.updateService(existingService);
    }
    
    // Historias de usuario del Director
    @Override
    public ServiceModel createSongListForService(String serviceId, String directorId, List<CreateSongListRequest> songs) {
        validateServiceExists(serviceId);
        validateDirectorPermission(serviceId, directorId);
        validateSongListData(songs);
        
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        List<SongsModel> songsList = createSongsList(songs);
        
        service.setSongsList(songsList);
        return servicesUseCases.updateService(service);
    }
    
    @Override
    public ServiceModel updateSongListForService(String serviceId, String directorId, List<CreateSongListRequest> songs) {
        validateServiceExists(serviceId);
        validateDirectorPermission(serviceId, directorId);
        validateSongListData(songs);
        
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        List<SongsModel> songsList = createSongsList(songs);
        
        service.setSongsList(songsList);
        return servicesUseCases.updateService(service);
    }
    
    // Consultas generales
    @Override
    public ServiceModel getServiceById(String serviceId) {
        if (serviceId == null || serviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de servicio no puede ser null o vacío");
        }
        return servicesUseCases.getServiceById(serviceId);
    }
    
    @Override
    public List<ServiceModel> getAllServices() {
        return servicesUseCases.getAllServices();
    }
    
    @Override
    public List<ServiceModel> getServicesByDirector(String directorId) {
        if (directorId == null || directorId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de director no puede ser null o vacío");
        }
        return servicesUseCases.getServicesByDirector(directorId);
    }
    
    @Override
    public List<ServiceModel> getServicesByMusician(String musicianId) {
        if (musicianId == null || musicianId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de músico no puede ser null o vacío");
        }
        return servicesUseCases.getServicesByMusician(musicianId);
    }
    
    @Override
    public List<ServiceModel> getServicesByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Fecha no puede ser null");
        }
        
        List<ServiceModel> allServices = servicesUseCases.getAllServices();
        return allServices.stream()
                .filter(service -> service.getServiceDate().equals(date))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ServiceModel> getServicesByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Fechas no pueden ser null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        List<ServiceModel> allServices = servicesUseCases.getAllServices();
        return allServices.stream()
                .filter(service -> !service.getServiceDate().isBefore(startDate) && 
                                 !service.getServiceDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    // Validaciones de roles dinámicos
    @Override
    public boolean isUserDirectorOfService(String userId, String serviceId) {
        if (userId == null || serviceId == null) {
            return false;
        }
        
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        if (service == null || service.getDirectors() == null) {
            return false;
        }
        
        return service.getDirectors().stream()
                .anyMatch(director -> director.getId().equals(userId));
    }
    
    
    
    
    // Eliminación automática (historia del sistema)
    @Override
    public void deleteExpiredServices() {
        LocalDate today = LocalDate.now();
        List<ServiceModel> allServices = servicesUseCases.getAllServices();
        
        List<String> expiredServiceIds = allServices.stream()
                .filter(service -> service.getServiceDate().isBefore(today))
                .map(ServiceModel::getId)
                .collect(Collectors.toList());
        
        for (String serviceId : expiredServiceIds) {
            servicesUseCases.deleteService(serviceId);
        }
    }
    
    // Métodos privados para optimización y reutilización
    
    private void validateServiceDates(ServiceModel service) {
        if (service.getServiceDate() == null) {
            throw new IllegalArgumentException("Fecha de servicio es obligatoria");
        }
        if (service.getServiceDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha de servicio no puede ser en el pasado");
        }
        if (service.getPracticeDate() != null && service.getPracticeDate().isAfter(service.getServiceDate())) {
            throw new IllegalArgumentException("Fecha de ensayo no puede ser posterior a la fecha de servicio");
        }
    }
    
    private void initializeServiceLists(ServiceModel service) {
        if (service.getDirectors() == null) {
            service.setDirectors(new ArrayList<>());
        }
        if (service.getMusiciansList() == null) {
            service.setMusiciansList(new ArrayList<>());
        }
        if (service.getSongsList() == null) {
            service.setSongsList(new ArrayList<>());
        }
    }
    
    private void validateServiceExists(String serviceId) {
        if (servicesUseCases.getServiceById(serviceId) == null) {
            throw new IllegalArgumentException("Servicio no encontrado: " + serviceId);
        }
    }
    
    private void validateDirectorIds(List<String> directorIds) {
        if (directorIds == null || directorIds.isEmpty()) {
            throw new IllegalArgumentException("Lista de directores no puede ser null o vacía");
        }
    }
    
   
    
    private void validateDirectorPermission(String serviceId, String directorId) {
        if (!isUserDirectorOfService(directorId, serviceId)) {
            throw new IllegalArgumentException("Usuario no tiene permisos de director para este servicio");
        }
    }
    
    private void validateSongListData(List<CreateSongListRequest> songs) {
        if (songs == null || songs.isEmpty()) {
            throw new IllegalArgumentException("La lista de canciones no puede ser null o vacía");
        }
    }
    
    private List<SongsModel> createSongsList(List<CreateSongListRequest> songs) {
        List<SongsModel> songsList = new ArrayList<>();
        
        for (CreateSongListRequest song : songs) {
            SongsModel songModel = new SongsModel();
            songModel.setName(song.getSongName());
            songModel.setArtist(song.getComposer());
            songModel.setYoutubeLink(song.getLink());
            songModel.setTone(song.getTonality());
            songsList.add(songModel);
        }
        
        return songsList;
    }

    /**
     * Registra las notificaciones generadas (temporalmente solo log)
     */
    private void logNotificationResults(
            List<INotificationService.EmailNotificationBody> assignmentNotifications,
            List<INotificationService.EmailNotificationBody> removalNotifications) {
        
        System.out.println("=== NOTIFICACIONES DE ASIGNACIÓN ===");
        for (INotificationService.EmailNotificationBody notification : assignmentNotifications) {
            System.out.println("📧 Email para: " + notification.getUserEmail());
            System.out.println("📋 Asunto: " + notification.getSubject());
            System.out.println("📝 Cuerpo: " + notification.getEmailBody());
            System.out.println("---");
        }
        
        System.out.println("=== NOTIFICACIONES DE REMOCIÓN ===");
        for (INotificationService.EmailNotificationBody notification : removalNotifications) {
            System.out.println("📧 Email para: " + notification.getUserEmail());
            System.out.println("📋 Asunto: " + notification.getSubject());
            System.out.println("📝 Cuerpo: " + notification.getEmailBody());
            System.out.println("---");
        }
    }
    
    /**
     * Genera notificaciones de asignación para la creación de un servicio
     */
    private void generateCreationNotifications(ServiceModel service, List<String> directorIds, List<MusicianAssignment> musicianAssignments) {
        try {
            // Crear objeto de asignaciones nuevas (lo que se está asignando)
            UpdateAssingmentRequest.Assignments newAssignments = new UpdateAssingmentRequest.Assignments();
            newAssignments.setDirectorIds(directorIds != null ? directorIds : new ArrayList<>());
            newAssignments.setMusiciansList(musicianAssignments != null ? musicianAssignments : new ArrayList<>());
            
            // Crear objeto de asignaciones viejas (vacío para creación)
            UpdateAssingmentRequest.Assignments oldAssignments = new UpdateAssingmentRequest.Assignments();
            oldAssignments.setDirectorIds(new ArrayList<>());
            oldAssignments.setMusiciansList(new ArrayList<>());
            
            // Generar notificaciones de asignación (solo assignments, no removals)
            List<INotificationService.EmailNotificationBody> assignmentNotifications = 
                notificationService.generateAssignmentNotifications(service, newAssignments, oldAssignments);
            
            // Log de resultados
            log.info("📧 {} notificaciones de asignación enviadas para la creación del servicio {}", 
                assignmentNotifications.size(), service.getId());
            
        } catch (Exception e) {
            log.error("❌ Error generando notificaciones de creación para servicio {}: {}", 
                service.getId(), e.getMessage());
            // No lanzamos la excepción para no afectar la creación del servicio
        }
    }
} 