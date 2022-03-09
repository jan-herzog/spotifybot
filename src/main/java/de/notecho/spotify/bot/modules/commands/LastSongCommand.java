package de.notecho.spotify.bot.modules.commands;

import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.bot.modules.Command;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.module.UserLevel;
import de.notecho.spotify.utils.SpotifyUtils;
import lombok.SneakyThrows;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class LastSongCommand extends Command {

    public LastSongCommand(Module module, BotInstance root) {
        super(module, root);
    }

    @Override
    @SneakyThrows
    public void exec(String userName, String id, UserLevel userLevel, String[] args) {
        PagingCursorbased<PlayHistory> historyPagingCursorbased = getRoot().getSpotifyApi().getCurrentUsersRecentlyPlayedTracks().limit(1).build().execute();
        if (historyPagingCursorbased == null) {
            sendMessage(getModule(ModuleType.SYSTEM).getEntry("spotifyNotReachable"), "$USER", userName);
            return;
        }
        String trackId = historyPagingCursorbased.getItems()[0].getTrack().getId();
        Track track = getRoot().getSpotifyApi().getTrack(trackId).build().execute();
        sendMessage(getModule().getEntry("lastSong"), "$USER", userName, "$SONG", track.getName(), "$ARTISTS", SpotifyUtils.getArtists(track));
    }
}
