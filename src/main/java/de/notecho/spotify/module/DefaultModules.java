package de.notecho.spotify.module;

import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public enum DefaultModules {

    SONG(
            ModuleType.SONG,
            Arrays.asList(
                    new DefaultEntry("playingSong", "$USER, Current song playing: $SONG - $ARTISTS", UserLevel.DEFAULT),
                    new DefaultEntry("notPlaying", "$USER, Spotify Playback is currently paused!", UserLevel.DEFAULT)
            )
    ),
    LASTSONG(
            ModuleType.LASTSONG,
            Collections.singletonList(
                    new DefaultEntry("lastSong", "$USER, Last song played: $LASTSONG - $ARTISTS", UserLevel.DEFAULT)
            )
    ),
    VOLUME(
            ModuleType.VOLUME,
            Arrays.asList(
                    new DefaultEntry("showVolume", "$USER, The current Spotify Volume is at $VOLUME.", UserLevel.MOD),
                    new DefaultEntry("setVolume", "$USER, The Spotify Volume is now set to $VOLUME", UserLevel.MOD)
            )
    ),
    PAUSE(
            ModuleType.PAUSE,
            Collections.singletonList(
                    new DefaultEntry("sPause", "$USER, Playback is paused now!", UserLevel.MOD)
            )
    ),
    PLAY(
            ModuleType.PLAY,
            Collections.singletonList(
                    new DefaultEntry("sPlay", "$USER, '$SONG - $ARTISTS' is playing now!", UserLevel.MOD)
            )
    ),
    PLAYADD(
            ModuleType.PLAYADD,
            Collections.singletonList(
                    new DefaultEntry("sPlayAdd", "$USER, '$SONG' was added to the Spotify Queue!", UserLevel.MOD)
            )
    ),
    SKIP(
            ModuleType.SKIP,
            Collections.singletonList(
                    new DefaultEntry("sSkip", "$USER, '$SONG' was skipped!", UserLevel.MOD)
            )
    ),
    PREVIOUS(
            ModuleType.PREVIOUS,
            Collections.singletonList(
                    new DefaultEntry("sPrevious", "$USER, '$SONG' is playing now!", UserLevel.MOD)
            )
    ),
    SONGREQUEST(
            ModuleType.SONGREQUEST,
            Arrays.asList(
                    new DefaultEntry("srRedeem", "Songrequest", UserLevel.DEFAULT),
                    new DefaultEntry("requested", "$USER, The Song '$SONG' was requested.", UserLevel.DEFAULT),
                    new DefaultEntry("notAvailable", "$USER, The Song '$SONG' is not available.", UserLevel.DEFAULT),
                    new DefaultEntry("exceededMaxLength", "$USER, The Song '$SONG' exceeded the maximum amount of time.", UserLevel.DEFAULT),
                    new DefaultEntry("notFound", "$USER, No matching song was found for this specific search query.", UserLevel.DEFAULT)
            )
    ),
    SYSTEM(
            ModuleType.SYSTEM,
            Arrays.asList(
                    new DefaultEntry("notAvailable", "$USER, The Song '$SONG' is not available.", UserLevel.DEFAULT),
                    new DefaultEntry("notFound", "$USER, No matching song was found for this specific search query.", UserLevel.DEFAULT),
                    new DefaultEntry("spotifyNotReachable", "$USER, Spotify isn't currently reachable!", UserLevel.DEFAULT),
                    new DefaultEntry("noPerms", "$USER, You can't do that! (Minimum Role: $ROLE)", UserLevel.DEFAULT),
                    new DefaultEntry("syntax", "$USER, Usage: $USAGE", UserLevel.DEFAULT),
                    new DefaultEntry("noSpotifyLinked", "$BROADCASTER didn't link his spotify yet.", UserLevel.DEFAULT)
            )
    );

    private final ModuleType commandType;

    private final List<DefaultEntry> entries;

    public Module createNewInstance() {
        final List<ModuleEntry> entries = new ArrayList<>();
        for (DefaultEntry entry : this.entries)
            entries.add(new ModuleEntry(0L, entry.getKey(), entry.getValue(), entry.getUserLevel()));
        return new Module(0L, entries, true, this.commandType);
    }

    public static List<Module> defaultList() {
        List<Module> list = new ArrayList<>();
        for (DefaultModules value : values())
            list.add(value.createNewInstance());
        return list;
    }

}
