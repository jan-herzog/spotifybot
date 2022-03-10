package de.notecho.spotify.bot.modules;

import com.github.philippheuer.events4j.api.domain.IEventSubscription;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.domain.ChannelPointsUser;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.database.user.entities.module.Module;

import java.util.function.Consumer;

public abstract class Reward extends BaseModule {

    public Reward(Module module, BotInstance root) {
        super(module, root);
    }

    private Consumer<RewardRedeemedEvent> channelPointsRedemptionEvent() {
        return event -> {
            String channelId = event.getRedemption().getChannelId();
            String message = event.getRedemption().getUserInput();
            if(!channelId.equals(getRoot().getId()))
                return;
            if(!event.getRedemption().getReward().getTitle().equalsIgnoreCase(getModule().getEntry(getModule().getModuleType().getTrigger()).getEntryValue()))
                return;
            ChannelPointsUser user = event.getRedemption().getUser();
            exec(user.getLogin(), user.getId(), message);
        };
    }

    @Override
    public void register(TwitchClient client) {
        client.getEventManager().onEvent(RewardRedeemedEvent.class, channelPointsRedemptionEvent());
    }

    @Override
    public void unregister(TwitchClient client) {
        for (IEventSubscription activeSubscription : client.getEventManager().getActiveSubscriptions())
            if(activeSubscription.getConsumer() == channelPointsRedemptionEvent())
                activeSubscription.dispose();
    }

    public abstract void exec(String userName, String id, String message);
}
