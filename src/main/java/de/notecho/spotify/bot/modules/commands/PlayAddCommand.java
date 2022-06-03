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
            sendMessage(getModule(ModuleType.SYSTEM).getEntry("noPerms"), "$USER", userName, "$ROLE", sPlayAdd.getUserLevel().getPrettyName());
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
        Track track = SpotifyUtils.getTrackFromString(searchQuery.toString(), getRoot().getSpotifyApi());
        if (track == null) {
            sendMessage(getModule().getEntry("notFound"), "$USER", userName);
            return;
        }
        CountryCode country = getRoot().getSpotifyApi().getCurrentUsersProfile().build().execute().getCountry();
        if (Arrays.stream(track.getAvailableMarkets()).noneMatch(countryCode -> countryCode.equals(country != null ? country : CountryCode.US))) {
            sendMessage(getModule(ModuleType.SONGREQUEST).getEntry("notAvailable"), "$USER", userName, "$SONG", track.getName());
            return;
        }
        getRoot().getSpotifyApi().addItemToUsersPlaybackQueue(track.getUri()).build().execute();
        sendMessage(sPlayAdd, "$USER", userName, "$SONG", track.getName());
    }
}
