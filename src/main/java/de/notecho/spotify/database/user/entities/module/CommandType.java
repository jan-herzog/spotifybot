package de.notecho.spotify.database.user.entities.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {

    SONG("song", null);

    private final String trigger;

    private final Class<?> commandClass;



}
