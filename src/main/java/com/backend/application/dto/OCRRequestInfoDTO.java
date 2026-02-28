package com.backend.application.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OCRRequestInfoDTO {
    //Fecha formato YYYY-MM-DD
    private LocalDate date;
    private List<MusicianAssignment> asignaciones;
}
