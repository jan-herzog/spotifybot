package de.notecho.spotify.module;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultEntry {

    private String key, value;

    private UserLevel userLevel;

}
