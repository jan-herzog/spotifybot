package de.notecho.spotify.bot;

import de.notecho.spotify.SpotifyBotApplication;
import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.repository.UserRepository;
import de.notecho.spotify.utils.logger.LogType;
import de.notecho.spotify.utils.logger.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BotInstanceManagementService {

    private final Environment environment;

    private final UserRepository userRepository;

    @Getter
    private final List<BotInstance> activeInstances = new ArrayList<>();

    @EventListener(ApplicationReadyEvent.class)
    public void fetchUsers(ApplicationReadyEvent event) {
        Logger.log(LogType.DEBUG, "Fetching users...", "users");
        for (BotUser user : userRepository.findAllByOrderByIdAsc())
            startInstance(user, event.getApplicationContext());
        Logger.log(LogType.DEBUG, "Fetched users!", "users");
    }

    public void startInstance(BotUser user, ApplicationContext context) {
        if (user.spotifyTokens() == null) //TODO: OR INVALID
            return;
        activeInstances.add(new BotInstance(user, environment, context));
    }

    public void startInstance(BotUser user) {
        startInstance(user, SpotifyBotApplication.getInstance());
    }

    public void stopInstance(BotUser user) {
        BotInstance instance = getInstance(user);
        instance.dispose();
        activeInstances.remove(instance);
    }

    public BotInstance getInstance(BotUser user) {
        return activeInstances.stream().filter(botInstance -> botInstance.getUser().equals(user)).findAny().orElse(null);
    }

    public void updateClient(BotUser user) {
        getInstance(user).updateClient();
    }

}
