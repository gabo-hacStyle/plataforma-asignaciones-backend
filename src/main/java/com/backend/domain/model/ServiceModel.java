package com.backend.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceModel {
    private LocalDate serviceDate;
    private LocalDate practiceDate;
    private List<UserModel> directors;
    private List<MusiciansList> musiciansList;
    private List<SongsModel> songsList;
    private String location;
    private String id;
    private LocalDateTime createdAt;
    private String clothesColor;
}
