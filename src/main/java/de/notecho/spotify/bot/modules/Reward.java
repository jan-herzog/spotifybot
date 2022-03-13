package de.notecho.spotify.bot.modules;

import com.github.philippheuer.events4j.api.domain.IEventSubscription;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.pubsub.domain.ChannelPointsUser;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.module.ModuleType;

import java.util.Collections;
import java.util.function.Consumer;

public abstract class Reward extends BaseModule {

    public Reward(Module module, BotInstance root) {
        super(module, root);
        setEventConsumer(channelPointsRedemptionEvent());
    }

    private Consumer<RewardRedeemedEvent> channelPointsRedemptionEvent() {
        return event -> {
            String channelId = event.getRedemption().getChannelId();
            String message = event.getRedemption().getUserInput();
            if (!channelId.equals(getRoot().getId()))
                return;
            if (!event.getRedemption().getReward().getTitle().equalsIgnoreCase(getModule().getEntry(getModule().getModuleType().getTrigger()).getEntryValue()))
                return;
            ChannelPointsUser user = event.getRedemption().getUser();
            try {
                exec(event, user.getLogin(), user.getId(), message);
            } catch (NullPointerException e) {
                setRedemptionStatus(event, RedemptionStatus.CANCELED);
                sendMessage(getModule(ModuleType.SYSTEM).getEntry("spotifyNotReachable"), "$USER", user.getLogin());
            } catch (Exception e) {
                setRedemptionStatus(event, RedemptionStatus.CANCELED);
                e.printStackTrace();
            }
        };
    }

    @Override
    public void register(TwitchClient client) {
        client.getEventManager().onEvent(RewardRedeemedEvent.class, (Consumer<RewardRedeemedEvent>) getEventConsumer());
    }

    @Override
    public void unregister(TwitchClient client) {
        for (IEventSubscription activeSubscription : client.getEventManager().getActiveSubscriptions())
            if (activeSubscription.getConsumer() == getEventConsumer())
                activeSubscription.dispose();
    }

    public abstract void exec(RewardRedeemedEvent event, String userName, String id, String message);

    protected void setRedemptionStatus(RewardRedeemedEvent event, RedemptionStatus status) {
        BotUser user = getRoot().getUser();
        getRoot().getClient().getHelix().updateRedemptionStatus(
                user.twitchTokens().getAccessToken(),
                user.getTwitchId(),
                event.getRedemption().getReward().getId(),
                Collections.singletonList(event.getRedemption().getId()),
                status
        ).execute();
    }

}
