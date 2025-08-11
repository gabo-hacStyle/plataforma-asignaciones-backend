package com.backend.infraestructure.adapters.in.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class SystemOperationResponse {
    private String operation;
    private String status;
    private String message;
}