package com.backend.infraestructure.adapters.out.entities;

import com.backend.domain.model.MusiciansList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusiciansListEntity {
    
    private List<UserEntity> musician;
    private String instrument;
    
    public static MusiciansListEntity fromDomain(MusiciansList musiciansList) {
        MusiciansListEntity entity = new MusiciansListEntity();
        entity.setMusician(musiciansList.getMusician().stream()
                .map(UserEntity::fromDomain)
                .collect(Collectors.toList()));
        entity.setInstrument(musiciansList.getInstrument());
        return entity;
    }
    
    public MusiciansList toDomain() {
        MusiciansList musiciansList = new MusiciansList();
        musiciansList.setMusician(this.musician.stream()
                .map(UserEntity::toDomain)
                .collect(Collectors.toList()));
        musiciansList.setInstrument(this.instrument);
        return musiciansList;
    }
} 