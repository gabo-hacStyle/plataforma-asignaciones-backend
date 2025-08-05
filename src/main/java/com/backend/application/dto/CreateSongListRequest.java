package com.backend.application.dto;



import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSongListRequest {
    private String songName;
    private String composer;
    private String link;
    private String tonality;
}
