package de.notecho.spotify.bot.modules;

import com.github.twitch4j.TwitchClient;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
import de.notecho.spotify.module.ModuleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public abstract class BaseModule {

    private final Module module;

    private final BotInstance root;

    @Setter
    private Consumer<?> eventConsumer;

    public abstract void register(TwitchClient client);

    public abstract void unregister(TwitchClient client);

    public void sendMessage(ModuleEntry entry, String... args) {
        if (entry.getEntryValue().equalsIgnoreCase("--"))
            return;
        String result = entry.getEntryValue();
        for (int i = 0; i < args.length; i += 2)
            result = result.replace(args[i], args[i + 1]);
        root.getClient().getChat().sendMessage(root.getLogin(), result);
    }

    public Module getModule(ModuleType type) {
        return getRoot().getUser().getModules().stream().filter(module -> module.getModuleType() == type).findAny().orElse(null);
    }

}
