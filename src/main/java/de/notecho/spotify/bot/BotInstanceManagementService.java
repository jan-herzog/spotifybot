package de.notecho.spotify.bot;

import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.repository.UserRepository;
import de.notecho.spotify.utils.logger.LogType;
import de.notecho.spotify.utils.logger.Logger;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BotInstanceManagementService {

    private final Environment environment;

    @Getter
    private final List<BotInstance> activeInstances = new ArrayList<>();

    @Autowired
    public BotInstanceManagementService(Environment environment, UserRepository userRepository) {
        this.environment = environment;
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Logger.log(LogType.DEBUG, "Fetching users in 15 seconds...", "users", "15");
        service.schedule(() -> {
            Logger.log(LogType.DEBUG, "Fetching users...", "users");
            for (BotUser user : userRepository.findAllByOrderByIdAsc())
                startInstance(user);
            Logger.log(LogType.DEBUG, "Fetched users!", "users");
        }, 15, TimeUnit.SECONDS);
    }

    public void startInstance(BotUser user) {
        if (user.spotifyTokens() == null) //TODO: OR INVALID
            return;
        activeInstances.add(new BotInstance(user, environment));
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
