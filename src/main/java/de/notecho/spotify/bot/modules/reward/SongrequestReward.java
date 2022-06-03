package de.notecho.spotify.bot.modules.reward;

import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import com.neovisionaries.i18n.CountryCode;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.bot.modules.Reward;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.utils.SpotifyUtils;
import lombok.SneakyThrows;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;

public class SongrequestReward extends Reward {

    public SongrequestReward(Module module, BotInstance root) {
        super(module, root);
    }

    @SneakyThrows
    @Override
    public void exec(RewardRedeemedEvent event, String userName, String id, String message) {
        Track track = SpotifyUtils.getTrackFromString(message, getRoot().getSpotifyApi());
        if (track == null) {
            sendMessage(getModule().getEntry("notFound"), "$USER", userName);
            setRedemptionStatus(event, RedemptionStatus.CANCELED);
            return;
        }
        CountryCode country = getRoot().getSpotifyApi().getCurrentUsersProfile().build().execute().getCountry();
        if (Arrays.stream(track.getAvailableMarkets()).noneMatch(countryCode -> countryCode.equals(country != null ? country : CountryCode.US))) {
            sendMessage(getModule(ModuleType.SONGREQUEST).getEntry("notAvailable"), "$USER", userName, "$SONG", track.getName());
            setRedemptionStatus(event, RedemptionStatus.CANCELED);
            return;
        }
        getRoot().getSpotifyApi().addItemToUsersPlaybackQueue(track.getUri()).build().execute();
        sendMessage(getModule().getEntry("requested"), "$USER", userName, "$SONG", track.getName());
    }


}
