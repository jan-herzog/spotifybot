package de.notecho.spotify.bot.modules.reward;

import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import com.neovisionaries.i18n.CountryCode;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.bot.modules.Reward;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.module.ModuleType;
import lombok.SneakyThrows;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;

public class SongrequestReward extends Reward {

    public SongrequestReward(Module module, BotInstance root) {
        super(module, root);
    }

    @SneakyThrows
    @Override
    public void exec(RewardRedeemedEvent event, String userName, String id, String message) {
        Paging<Track> search = getRoot().getSpotifyApi().searchTracks(message).build().execute();
        if (search.getTotal() == 0) {
            sendMessage(getModule().getEntry("notFound"), "$USER", userName);
            setRedemptionStatus(event, RedemptionStatus.CANCELED);
            return;
        }
        String uri = search.getItems()[0].getUri();
        Track track = getRoot().getSpotifyApi().getTrack(uri.replace("spotify:track:", "")).build().execute();
        if (Arrays.stream(track.getAvailableMarkets()).noneMatch(countryCode -> countryCode.equals(CountryCode.DE))) {
            sendMessage(getModule(ModuleType.SONGREQUEST).getEntry("notAvailable"), "$USER", userName, "$SONG", track.getName());
            setRedemptionStatus(event, RedemptionStatus.CANCELED);
            return;
        }
        getRoot().getSpotifyApi().addItemToUsersPlaybackQueue(uri).build().execute();
        sendMessage(getModule().getEntry("requested"), "$USER", userName, "$SONG", track.getName());
    }
}
