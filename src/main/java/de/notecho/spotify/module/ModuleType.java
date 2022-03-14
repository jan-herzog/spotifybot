package de.notecho.spotify.module;

import de.notecho.spotify.bot.modules.commands.*;
import de.notecho.spotify.bot.modules.reward.SongrequestReward;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleType {

    SONG("song", SongCommand.class),
    LASTSONG("lastsong", LastSongCommand.class),
    VOLUME("svolume", VolumeCommand.class),
    PAUSE("spause", null),
    PLAY("splay", PlayCommand.class),
    PLAYADD("splayadd", PlayAddCommand.class),
    SKIP("sskip", null),
    PREVIOUS("sprevious", null),
    SONGREQUEST("srRedeem", SongrequestReward.class),
    SYSTEM(null, null);

    private final String trigger;

    private final Class<?> moduleClass;


}
