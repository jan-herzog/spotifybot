package de.notecho.spotify.bot.instance;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.User;
import de.notecho.spotify.bot.BotInstanceManagementService;
import de.notecho.spotify.bot.modules.BaseModule;
import de.notecho.spotify.config.BotConfiguration;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.entities.TokenPair;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.repository.UserRepository;
import de.notecho.spotify.module.TokenType;
import de.notecho.spotify.utils.logger.LogType;
import de.notecho.spotify.utils.logger.Logger;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.BadRequestException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class BotInstance {

    private BotUser user;

    private TwitchClient client = null;

    private final SpotifyApi spotifyApi;

    private final List<BaseModule> modules = new ArrayList<>();

    private String id, login;

    private final ApplicationContext context;

    private long nextCheck = System.currentTimeMillis();

    @SneakyThrows
    public BotInstance(BotUser user, Environment environment, ApplicationContext context) {
        long start = System.currentTimeMillis();
        this.context = context;
        this.user = user;
        System.out.println(user.spotifyTokens().getId());
        System.out.println(user.spotifyTokens().getAccessToken());
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(environment.getProperty("spotify.clientId"))
                .setClientSecret(environment.getProperty("spotify.clientSecret"))
                .setAccessToken(user.spotifyTokens().getAccessToken())
                .setRefreshToken(user.spotifyTokens().getRefreshToken())
                .setRedirectUri(SpotifyHttpManager.makeUri(environment.getProperty("spotify.uri")))
                .build();
        updateClient(true);
        updateTokens();
        User twitchUser = client.getHelix().getUsers(user.twitchTokens().getAccessToken(), null, null).execute().getUsers().get(0);
        this.login = twitchUser.getLogin();
        this.id = user.getTwitchId();
        for (Module module : this.user.getModules()) {
            if (module.getModuleType().getModuleClass() == null)
                continue;
            if (module.isEnabled())
                this.modules.add((BaseModule) module.getModuleType().getModuleClass().getConstructor(Module.class, BotInstance.class).newInstance(module, this));
        }
        start();
        Logger.log(LogType.DEBUG, "[" + user.getId() + "] Started BotInstance(" + twitchUser.getLogin() + ", " + user.getTwitchId() + ") in " + (System.currentTimeMillis() - start) + "ms.", twitchUser.getLogin(), user.getTwitchId(), (System.currentTimeMillis() - start) + "ms");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (nextCheck <= System.currentTimeMillis())
                    try {
                        updateTokens();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }, 0, 1000);
    }


    private void updateTokens() {
        if (user.spotifyTokens() == null) {
            dispose();
            return;
        }
        updateTwitchToken(user.twitchTokens());
        if (user.chatAccountTokens() != null)
            updateTwitchToken(user.chatAccountTokens());
        try {
            this.spotifyApi.setRefreshToken(user.spotifyTokens().getRefreshToken());
            AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            user.spotifyTokens().setAccessToken(authorizationCodeCredentials.getAccessToken());
            Logger.log(LogType.DEBUG, "[" + user.getId() + "] Got new Spotify Token(" + user.spotifyTokens().getId() + ") for " + login + ", expires in: " + authorizationCodeCredentials.getExpiresIn() + " | " + authorizationCodeCredentials.getAccessToken().substring(0, 10) + "...", "Spotify", login, String.valueOf(authorizationCodeCredentials.getExpiresIn()), authorizationCodeCredentials.getAccessToken().substring(0, 10) + "...");
        } catch (BadRequestException e) {
            TokenPair spotifyTokens = user.spotifyTokens();
            user.getTokenPairs().remove(spotifyTokens);
            saveUser();
            context.getBean(BotInstanceManagementService.class).stopInstance(user);
            dispose();
            Logger.log(LogType.INFO, "[" + user.getId() + "] " + login + " revoked his access token so it was removed from the database.", login, "revoked", "database");
            return;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        saveUser();
        nextCheck = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(50);
    }

    private void updateTwitchToken(TokenPair tokenPair) {
        OAuth2Credential credential = new OAuth2Credential("twitch", tokenPair.getAccessToken());
        credential.setRefreshToken(tokenPair.getRefreshToken());
        Optional<OAuth2Credential> credentialOptional = context.getBean(OAuth2IdentityProvider.class).refreshCredential(credential);
        if (credentialOptional.isPresent()) {
            credential = credentialOptional.get();
            tokenPair.setAccessToken(credential.getAccessToken());
            tokenPair.setRefreshToken(credential.getRefreshToken());
            if (client == null)
                return;
            User twitchUser = client.getHelix().getUsers(credential.getAccessToken(), null, null).execute().getUsers().get(0);
            if (tokenPair.getTokenType().equals(TokenType.TWITCH)) {
                id = twitchUser.getId();
                login = twitchUser.getLogin();
            }
            Logger.log(LogType.DEBUG, "[" + user.getId() + "] Got new Twitch Token(" + tokenPair.getTokenType().name() + ") for " + twitchUser.getLogin() + " | " + credential.getAccessToken().substring(0, 20), "Twitch", tokenPair.getTokenType().name(), login, credential.getAccessToken());
        }
    }

    public void updateClient() {
        updateClient(false);
    }

    private void updateClient(boolean init) {
        if (!init) {
            user = context.getBean(UserRepository.class).getById(user.getId());
            dispose();
        }
        if (user.chatAccountTokens() == null)
            this.client = context.getBean(TwitchClient.class);
        else {
            updateTwitchToken(user.chatAccountTokens());
            this.client = TwitchClientBuilder.builder()
                    .withEnableHelix(true)
                    .withEnablePubSub(true)
                    .withEnableChat(true)
                    .withCredentialManager(context.getBean(BotConfiguration.class).getAccountCredentialManager())
                    .withDefaultAuthToken(new OAuth2Credential("twitch", "oauth:" + user.chatAccountTokens().getAccessToken()))
                    .withChatAccount(new OAuth2Credential("twitch", "oauth:" + user.chatAccountTokens().getAccessToken()))
                    .build();
        }
        if (!init)
            start();
        TokenType tokenType = user.chatAccountTokens() == null ? TokenType.TWITCH : TokenType.CHATACCOUNT;
        Logger.log(LogType.INFO, "[" + user.getId() + "] Update on chat account. New TokenType: " + tokenType.name(), "Update", tokenType.name());
    }

    public void start() {
        this.client.getChat().joinChannel(this.login);
        for (BaseModule module : this.modules)
            module.register(client);
    }

    public void dispose() {
        for (BaseModule module : this.modules)
            module.unregister(client);
        client.getChat().leaveChannel(this.login);
        context.getBean(BotInstanceManagementService.class).getActiveInstances().remove(this);
    }

    public void saveUser() {
        context.getBean(UserRepository.class).saveAndFlush(user);
    }

    @SneakyThrows
    public void updateModules(BotUser user) {
        this.user = user;
        dispose();
        this.modules.clear();
        for (Module module : this.user.getModules()) {
            if (module.getModuleType().getModuleClass() == null)
                continue;
            if (module.isEnabled())
                this.modules.add((BaseModule) module.getModuleType().getModuleClass().getConstructor(Module.class, BotInstance.class).newInstance(module, this));
        }
    }

}
