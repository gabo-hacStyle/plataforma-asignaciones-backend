package com.backend.application.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateServiceRequest {
    private LocalDate serviceDate;
    private LocalDate practiceDate;
    private String location;
    private List<String> directorIds;
    private List<MusicianAssignment> musicianAssignments;
    
    
}
