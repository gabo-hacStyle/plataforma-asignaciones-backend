package com.backend.application.dto;

import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssingmentRequest {

    private Assignments oldAssignments;
    private Assignments newAssignments;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Assignments {
        private List<String> directorIds;   
        private List<MusicianAssignment> musiciansList;
    }
    
}
