package com.backend.infraestructure.adapters.out.entities;

import com.backend.domain.model.SongsModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongsEntity {
    
    private String youtubeLink;
    private String tone;
    private String name;
    private String artist;
    
    public static SongsEntity fromDomain(SongsModel songsModel) {
        SongsEntity entity = new SongsEntity();
        entity.setYoutubeLink(songsModel.getYoutubeLink());
        entity.setTone(songsModel.getTone());
        entity.setName(songsModel.getName());
        entity.setArtist(songsModel.getArtist());
        return entity;
    }
    
    public SongsModel toDomain() {
        SongsModel songsModel = new SongsModel();
        songsModel.setYoutubeLink(this.youtubeLink);
        songsModel.setTone(this.tone);
        songsModel.setName(this.name);
        songsModel.setArtist(this.artist);
        return songsModel;
    }
} 