package com.backend.application;

import com.backend.domain.model.ServiceModel;

public interface IDirectorService {
    ServiceModel updateClothesColorForService(String serviceId, String clothesColor);
}
