package com.backend.domain.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongsModel {
    
    private String youtubeLink;
    private String tone;
    private String name;
    private String artist;
}
