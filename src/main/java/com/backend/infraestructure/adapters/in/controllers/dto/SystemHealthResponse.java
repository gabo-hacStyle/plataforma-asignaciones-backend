package com.backend.infraestructure.adapters.in.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemHealthResponse {
 

    
    
        private String status;
        private String timestamp;
        private String version;
        private String error; 
    
}
