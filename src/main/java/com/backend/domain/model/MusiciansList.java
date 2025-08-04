package com.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusiciansList {
    private UserModel musician;
    private String instrument;
}
