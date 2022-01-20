package de.notecho.spotify.bot.instance;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.User;
import de.notecho.spotify.SpotifyBotApplication;
import de.notecho.spotify.bot.modules.BaseModule;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.entities.module.Module;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BotInstance {

    private final BotUser user;

    private final TwitchClient client;

    private final SpotifyApi spotifyApi;

    private final List<BaseModule> modules = new ArrayList<>();

    private final String id, login;

    @SneakyThrows
    public BotInstance(BotUser user, Environment environment) {
        ApplicationContext context = SpotifyBotApplication.getInstance();
        this.user = user;
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(environment.getProperty("spotify.clientId"))
                .setClientSecret(environment.getProperty("spotify.clientSecret"))
                .setRedirectUri(SpotifyHttpManager.makeUri("https://spitchbot.com/spotify/callback"))
                .build();
        if (user.chatAccountTokens() == null)
            this.client = context.getBean(TwitchClient.class);
        else
            this.client = TwitchClientBuilder.builder()
                    .withEnablePubSub(true)
                    .withEnableChat(true)
                    .withDefaultAuthToken(new OAuth2Credential("twitch", "oauth:" + user.twitchTokens().getAccessToken()))
                    .withChatAccount(new OAuth2Credential("twitch", "oauth:" + user.twitchTokens().getAccessToken()))
                    .build();
        User twitchUser = client.getHelix().getUsers(null, null, null).execute().getUsers().get(0);
        this.login = twitchUser.getLogin();
        this.client.getChat().joinChannel(this.login);
        this.id = twitchUser.getId();
        for (Module module : this.user.getModules()) {
            this.modules.add((BaseModule) module.getModuleType().getCommandClass().getConstructor(Module.class, BotInstance.class).newInstance(module, this));
        }

    }

}
