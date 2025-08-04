package com.backend.infraestructure.adapters.out.entities;

import com.backend.domain.model.MusiciansList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusiciansListEntity {
    
    private UserEntity musician;
    private String instrument;
    
    public static MusiciansListEntity fromDomain(MusiciansList musiciansList) {
        MusiciansListEntity entity = new MusiciansListEntity();
        entity.setMusician(UserEntity.fromDomain(musiciansList.getMusician()));
        entity.setInstrument(musiciansList.getInstrument());
        return entity;
    }
    
    public MusiciansList toDomain() {
        MusiciansList musiciansList = new MusiciansList();
        musiciansList.setMusician(this.musician.toDomain());
        musiciansList.setInstrument(this.instrument);
        return musiciansList;
    }
} 