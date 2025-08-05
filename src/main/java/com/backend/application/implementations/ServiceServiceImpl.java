package com.backend.application.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.application.IServiceService;
import com.backend.application.dto.CreateSongListRequest;
import com.backend.application.dto.MusicianAssignment;
import com.backend.application.dto.UpdateAssingmentRequest;
import com.backend.domain.model.MusiciansList;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.model.SongsModel;
import com.backend.domain.model.UserModel;

import com.backend.domain.port.ServicesUseCases;
import com.backend.domain.port.UserUseCases;

@Service
public class ServiceServiceImpl implements IServiceService {
    
    @Autowired
    private ServicesUseCases servicesUseCases;
    
    @Autowired
    private UserUseCases userUseCases;
    
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
    public ServiceModel createServiceWithAssignments(LocalDate serviceDate, LocalDate practiceDate, String location,
                                                   List<String> directorIds, List<MusicianAssignment> musicianAssignments) {
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
                
                // Cambiar rol del usuario a DIRECTOR si es músico
                if (director.getRole() == UserModel.Role.MUSICIAN) {
                    director.setRole(UserModel.Role.DIRECTOR);
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
                UserModel musician = userUseCases.getUserById(assignment.getMusicianId());
                if (musician == null) {
                    throw new IllegalArgumentException("Músico no encontrado: " + assignment.getMusicianId());
                }
                
                MusiciansList musicianAssignment = new MusiciansList();
                musicianAssignment.setMusician(musician);
                musicianAssignment.setInstrument(assignment.getInstrument());
                musiciansList.add(musicianAssignment);
            }
            service.setMusiciansList(musiciansList);
        }
        
        // Crear el servicio en la base de datos
        return servicesUseCases.createService(service);
    }
    
    @Override
    public List<UserModel> getAllMusicians() {
        List<UserModel> allUsers = userUseCases.getAllUsers();
        return allUsers.stream()
                .filter(user -> user.getRole() == UserModel.Role.MUSICIAN)
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
            
            // Cambiar rol del usuario a DIRECTOR si es músico
            if (director.getRole() == UserModel.Role.MUSICIAN) {
                director.setRole(UserModel.Role.DIRECTOR);
                userUseCases.updateUser(director);
            }
            
            directors.add(director);
        }
        
        service.setDirectors(directors);
        return servicesUseCases.updateService(service);
    }
    
        @Override
        public ServiceModel assignMusiciansToService(String serviceId, List<MusicianAssignment> musicianAssignments) {
        validateServiceExists(serviceId);
       
        
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        List<MusiciansList> musiciansList = new ArrayList<>();
        
        for (MusicianAssignment assignment : musicianAssignments) {
            UserModel musician = userUseCases.getUserById(assignment.getMusicianId());
            if (musician == null) {
                throw new IllegalArgumentException("Músico no encontrado: " + assignment.getMusicianId());
            }
            
            MusiciansList musicianAssignment = new MusiciansList();
            musicianAssignment.setMusician(musician);
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
        
        // Actualizar directores
        if (request.getDirectorIds() != null && !request.getDirectorIds().isEmpty()) {
            service = assignDirectorsToService(serviceId, request.getDirectorIds());
        }
        
        // Actualizar músicos
        if (request.getMusiciansList() != null && !request.getMusiciansList().isEmpty()) {
            service = assignMusiciansToService(serviceId, request.getMusiciansList());
        }
        
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
    
    @Override
    public boolean isUserMusicianOfService(String userId, String serviceId) {
        if (userId == null || serviceId == null) {
            return false;
        }
        
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        if (service == null || service.getMusiciansList() == null) {
            return false;
        }
        
        return service.getMusiciansList().stream()
                .anyMatch(musician -> musician.getMusician().getId().equals(userId));
    }
    
    @Override
    public boolean isUserAdmin(String userId) {
        if (userId == null) {
            return false;
        }
        
        UserModel user = userUseCases.getUserById(userId);
        return user != null && user.getRole() == UserModel.Role.ADMIN;
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
} 