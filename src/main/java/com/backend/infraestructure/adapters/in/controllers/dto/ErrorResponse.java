package com.backend.infraestructure.adapters.in.controllers.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
     private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
    
}
