package com.backend.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicianAssignment {
    private String musicianId;
    private String instrument;
}
