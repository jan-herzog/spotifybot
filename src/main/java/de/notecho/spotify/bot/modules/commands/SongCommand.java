package de.notecho.spotify.bot.modules.commands;

import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.bot.modules.Command;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.module.UserLevel;
import de.notecho.spotify.utils.SpotifyUtils;
import lombok.SneakyThrows;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class SongCommand extends Command {

    public SongCommand(Module module, BotInstance root) {
        super(module, root);
    }

    @Override
    @SneakyThrows
    public void exec(String userName, String id, UserLevel userLevel, String[] args) {
        CurrentlyPlaying currentlyPlaying = getRoot().getSpotifyApi().getUsersCurrentlyPlayingTrack().build().execute();
        System.out.println(getRoot().getSpotifyApi().getCurrentUsersProfile().build().execute().getDisplayName());
        if (!currentlyPlaying.getIs_playing()) {
            sendMessage(getModule().getEntry("notPlaying"), "$USER", userName);
            return;
        }
        String uri = SpotifyUtils.getUriFromJson(getRoot().getSpotifyApi().getUsersCurrentlyPlayingTrack().build().getJson());
        Track track = getRoot().getSpotifyApi().getTrack(SpotifyUtils.getIdFromUri(uri)).build().execute();
        sendMessage(getModule().getEntry("playingSong"), "$USER", userName, "$SONG", track.getName(), "$ARTISTS", SpotifyUtils.getArtists(track));
    }
}
