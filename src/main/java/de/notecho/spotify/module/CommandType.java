package de.notecho.spotify.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {

    SONG("song", null),
    LASTSONG("lastsong", null),
    SVOLUME("svolume", null),
    SPAUSE("spause", null),
    SPLAY("splay", null),
    SPLAYADD("splayadd", null),
    SSKIP("sskip", null),
    SPREVIOUS("sprevious", null),
    ;

    private final String trigger;

    private final Class<?> commandClass;



}
