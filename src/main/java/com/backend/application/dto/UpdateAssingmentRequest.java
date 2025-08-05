package com.backend.application.dto;

import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssingmentRequest {
    private List<String> directorIds;   
    private List<MusicianAssignment> musiciansList;
}
