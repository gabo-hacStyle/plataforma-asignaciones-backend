package com.backend.domain.port;

import java.util.List;

import com.backend.domain.model.ServiceModel;

public interface ServicesUseCases {
    ServiceModel createService(ServiceModel service);
    ServiceModel getServiceById(String id);
    ServiceModel updateService(ServiceModel service);
    void deleteService(String id);
    List<ServiceModel> getAllServices();
    List<ServiceModel> getServicesByDirector(String directorId);
    List<ServiceModel> getServicesByMusician(String musicianId);
    //List<ServiceModel> getServicesByLocation(String location);
    //List<ServiceModel> getServicesByDate(LocalDate date);
    //List<ServiceModel> getServicesByDateRange(LocalDate startDate, LocalDate endDate);
}
