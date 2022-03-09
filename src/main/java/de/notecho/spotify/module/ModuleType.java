package de.notecho.spotify.module;

import de.notecho.spotify.bot.modules.commands.LastSongCommand;
import de.notecho.spotify.bot.modules.commands.PlayAddCommand;
import de.notecho.spotify.bot.modules.commands.SongCommand;
import de.notecho.spotify.bot.modules.commands.VolumeCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleType {

    SONG("song", SongCommand.class),
    LASTSONG("lastsong", LastSongCommand.class),
    VOLUME("svolume", VolumeCommand.class),
    PAUSE("spause", null),
    PLAY("splay", null),
    PLAYADD("splayadd", PlayAddCommand.class),
    SKIP("sskip", null),
    PREVIOUS("sprevious", null),
    SONGREQUEST(null, null),
    SYSTEM(null, null);

    private final String trigger;

    private final Class<?> commandClass;


}
