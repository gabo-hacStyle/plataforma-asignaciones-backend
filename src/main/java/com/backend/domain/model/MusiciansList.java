package com.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusiciansList {
    private List<UserModel> musician;
    private String instrument;
}
