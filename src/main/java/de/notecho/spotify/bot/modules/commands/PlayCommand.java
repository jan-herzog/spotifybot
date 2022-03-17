package de.notecho.spotify.bot.modules.commands;

import com.neovisionaries.i18n.CountryCode;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.bot.modules.Command;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.module.UserLevel;
import de.notecho.spotify.utils.SpotifyUtils;
import lombok.SneakyThrows;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;

public class PlayCommand extends Command {

    public PlayCommand(Module module, BotInstance root) {
        super(module, root);
    }

    @SneakyThrows
    @Override
    public void exec(String userName, String id, UserLevel userLevel, String[] args) {
        ModuleEntry sPlay = getModule().getEntry("sPlay");
        if (!userLevel.isHigherOrEquals(sPlay.getUserLevel())) {
            sendMessage(getModule(ModuleType.SYSTEM).getEntry("noPerms"), "$USER", userName, "$ROLE", sPlay.getUserLevel().getPrettyName());
            return;
        }
        if (args.length >= 1) {
            StringBuilder searchQuery = new StringBuilder();
            for (int i = 0; i < args.length; i++)
                if (i != 0)
                    searchQuery.append(" ");
                else
                    searchQuery.append(args[i]);
            Paging<Track> search = getRoot().getSpotifyApi().searchTracks(searchQuery.toString()).build().execute();
            if (search.getTotal() == 0) {
                sendMessage(getModule(ModuleType.SONGREQUEST).getEntry("notFound"), "$USER", userName);
                return;
            }
            String uri = search.getItems()[0].getUri();
            CountryCode country = getRoot().getSpotifyApi().getCurrentUsersProfile().build().execute().getCountry();
            Track track = getRoot().getSpotifyApi().getTrack(uri.replace("spotify:track:", "")).build().execute();
            if (Arrays.stream(track.getAvailableMarkets()).noneMatch(countryCode -> countryCode.equals(country != null ? country : CountryCode.US))) {
                sendMessage(getModule(ModuleType.SONGREQUEST).getEntry("notAvailable"), "$USER", userName, "$SONG", track.getName());
                return;
            }
            getRoot().getSpotifyApi().addItemToUsersPlaybackQueue(uri).build().execute();
            getRoot().getSpotifyApi().skipUsersPlaybackToNextTrack().build().execute();
            sendMessage(sPlay, "$USER", userName, "$SONG", track.getName());
            return;
        }
        getRoot().getSpotifyApi().startResumeUsersPlayback().build().execute();
        String uri = SpotifyUtils.getUriFromJson(getRoot().getSpotifyApi().getUsersCurrentlyPlayingTrack().build().getJson());
        Track track = getRoot().getSpotifyApi().getTrack(SpotifyUtils.getIdFromUri(uri)).build().execute();
        sendMessage(sPlay, "$USER", userName, "$SONG", track.getName(), "$ARTISTS", SpotifyUtils.getArtists(track));
    }
}
