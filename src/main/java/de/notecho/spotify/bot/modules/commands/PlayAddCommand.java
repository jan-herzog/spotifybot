package de.notecho.spotify.bot.modules.commands;

import com.neovisionaries.i18n.CountryCode;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.bot.modules.Command;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.module.UserLevel;
import lombok.SneakyThrows;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;

public class PlayAddCommand extends Command {
    public PlayAddCommand(Module module, BotInstance root) {
        super(module, root);
    }

    @SneakyThrows
    @Override
    public void exec(String userName, String id, UserLevel userLevel, String[] args) {
        ModuleEntry sPlayAdd = getModule().getEntry("sPlayAdd");
        if (!userLevel.isHigherOrEquals(sPlayAdd.getUserLevel())) {
            sendMessage(getModule(ModuleType.SYSTEM).getEntry("noPerms"), "$USER", userName, "$ROLE", userLevel.getPrettyName());
            return;
        }
        StringBuilder searchQuery = new StringBuilder();
        if (args.length >= 1)
            for (int i = 0; i < args.length; i++)
                if (i != 0)
                    searchQuery.append(" ");
                else
                    searchQuery.append(args[i]);
        else {
            sendMessage(getModule(ModuleType.SYSTEM).getEntry("syntax"), "$USER", userName, "$USAGE", "!sPlayAdd [song]");
            return;
        }

        Paging<Track> search = getRoot().getSpotifyApi().searchTracks(searchQuery.toString()).build().execute();
        if (search.getTotal() == 0) {
            sendMessage(getModule(ModuleType.SONGREQUEST).getEntry("notFound"), "$USER", userName);
            return;
        }
        String uri = search.getItems()[0].getUri();
        CountryCode country = getRoot().getSpotifyApi().getCurrentUsersProfile().build().execute().getCountry();
        Track track = getRoot().getSpotifyApi().getTrack(uri.replace("spotify:track:", "")).build().execute();
        if (Arrays.stream(track.getAvailableMarkets()).noneMatch(countryCode -> countryCode.equals(country))) {
            sendMessage(getModule(ModuleType.SONGREQUEST).getEntry("notAvailable"), "$USER", userName, "$SONG", track.getName());
            return;
        }
        getRoot().getSpotifyApi().addItemToUsersPlaybackQueue(uri).build().execute();
        sendMessage(sPlayAdd, "$USER", userName, "$SONG", track.getName());
    }
}
