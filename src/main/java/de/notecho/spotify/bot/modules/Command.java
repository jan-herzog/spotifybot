package de.notecho.spotify.bot.modules;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.module.UserLevel;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class Command extends BaseModule {

    private long lastUse = 0L;

    public Command(Module module, BotInstance root) {
        super(module, root);
    }

    private Consumer<ChannelMessageEvent> channelMessageEvent() {
        return event -> {
            String message = event.getMessage();
            EventChannel channel = event.getChannel();
            if (!channel.getId().equals(getRoot().getId()))
                return;
            EventUser user = event.getUser();
            if (!message.startsWith("!"))
                return;
            String[] messageArray = message.split(" ");
            String[] args = new String[0];
            if (messageArray.length >= 2)
                args = Arrays.copyOfRange(messageArray, 1, messageArray.length);
            String command = messageArray[0];
            if (getModule().getModuleType().getTrigger().equalsIgnoreCase(command.replace("!", ""))) {
                UserLevel userLevel = UserLevel.get(event.getPermissions());
                if (!isCooldown() || userLevel.isHigherOrEquals(UserLevel.MOD))
                    try {
                        exec(user.getName(), user.getId(), userLevel, args);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        sendMessage(getModule(ModuleType.SYSTEM).getEntry("spotifyNotReachable"), "$USER", user.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        };
    }

    private boolean isCooldown() {
        if (lastUse + TimeUnit.SECONDS.toMillis(10) <= System.currentTimeMillis()) {
            lastUse = System.currentTimeMillis();
            return false;
        }
        return true;
    }

    @Override
    public void register(TwitchClient client) {
        client.getEventManager().onEvent(ChannelMessageEvent.class, channelMessageEvent());
    }

    public abstract void exec(String userName, String id, UserLevel userLevel, String[] args);

}
