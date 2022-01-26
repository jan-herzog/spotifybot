package de.notecho.spotify.bot;

import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BotInstanceManagementService {

    private final Environment environment;

    @Getter
    private final List<BotInstance> activeInstances = new ArrayList<>();

    @Autowired
    public BotInstanceManagementService(Environment environment, UserRepository userRepository) {
        this.environment = environment;
        for (BotUser user : userRepository.findAllByOrderByIdAsc())
            startInstance(user);
    }

    public void startInstance(BotUser user) {
        if (user.spotifyTokens() == null) //TODO: OR INVALID
            return;
        activeInstances.add(new BotInstance(user, environment));
    }

    public void stopInstance(BotUser user) {
        activeInstances.removeIf(botInstance -> botInstance.getUser().equals(user));
    }

    public BotInstance getInstance(BotUser user) {
        return activeInstances.stream().filter(botInstance -> botInstance.getUser().equals(user)).findAny().orElse(null);
    }

}
