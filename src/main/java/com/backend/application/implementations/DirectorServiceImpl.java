package com.backend.application.implementations;

import com.backend.application.IDirectorService;
import com.backend.domain.model.ServiceModel;
import com.backend.domain.port.ServicesUseCases;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class DirectorServiceImpl implements IDirectorService {

    private final ServicesUseCases servicesUseCases;

    @Override
    @Transactional
    public ServiceModel updateClothesColorForService(String serviceId, String clothesColor) {
        ServiceModel service = servicesUseCases.getServiceById(serviceId);
        if (service == null) {
            throw new IllegalArgumentException("Servicio no encontrado: " + serviceId);
        }
        service.setClothesColor(clothesColor);
        return servicesUseCases.updateService(service);
    }
}
