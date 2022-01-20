package de.notecho.spotify.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleType {

    SONG("song", null),
    LASTSONG("lastsong", null),
    VOLUME("svolume", null),
    PAUSE("spause", null),
    PLAY("splay", null),
    PLAYADD("splayadd", null),
    SKIP("sskip", null),
    PREVIOUS("sprevious", null),
    SONGREQUEST(null, null),
    SYSTEM(null, null);

    private final String trigger;

    private final Class<?> commandClass;


}
