package de.notecho.spotify.web.session;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.notecho.spotify.database.user.entities.BotUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SessionManagementService {

    private final Cache<BotUser, String> sessions = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();

    public String getSession(BotUser user) {
        return sessions.getIfPresent(user);
    }

    public BotUser getUser(String session) {
        Map.Entry<BotUser, String> entry = sessions.asMap().entrySet().stream().filter(e -> e.getValue().equals(session)).findAny().orElse(null);
        return entry == null ? null : entry.getKey();
    }

    public boolean hasSession(BotUser user) {
        return sessions.getIfPresent(user) != null;
    }

    public void createSession(BotUser user) {
        if(!hasSession(user))
            sessions.put(user, UUID.randomUUID().toString());
    }

}
