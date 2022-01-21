package de.notecho.spotify.module;

import de.notecho.spotify.bot.modules.commands.LastSongCommand;
import de.notecho.spotify.bot.modules.commands.SongCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleType {

    SONG("song", SongCommand.class),
    LASTSONG("lastsong", LastSongCommand.class),
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
