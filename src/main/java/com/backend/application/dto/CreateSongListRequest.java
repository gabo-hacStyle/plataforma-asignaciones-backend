package com.backend.application.dto;

import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSongListRequest {
    private String songName;
    private String composer;
    private String musicalLink;
    private String tonality;
}
