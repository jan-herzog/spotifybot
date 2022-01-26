package de.notecho.spotify.bot.instance;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.User;
import de.notecho.spotify.SpotifyBotApplication;
import de.notecho.spotify.bot.BotInstanceManagementService;
import de.notecho.spotify.bot.modules.BaseModule;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.entities.module.Module;
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

    private final BotUser user;

    private final TwitchClient client;

    private final SpotifyApi spotifyApi;

    private final List<BaseModule> modules = new ArrayList<>();

    private String id, login;

    private final OAuth2IdentityProvider oAuth2IdentityProvider;

    private final BotInstanceManagementService botInstanceManagementService;

    private long nextCheck = System.currentTimeMillis();

    @SneakyThrows
    public BotInstance(BotUser user, Environment environment) {
        ApplicationContext context = SpotifyBotApplication.getInstance();
        this.user = user;
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(environment.getProperty("spotify.clientId"))
                .setClientSecret(environment.getProperty("spotify.clientSecret"))
                .setAccessToken(user.spotifyTokens().getAccessToken())
                .setRefreshToken(user.spotifyTokens().getRefreshToken())
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
        this.oAuth2IdentityProvider = context.getBean(OAuth2IdentityProvider.class);
        this.botInstanceManagementService = context.getBean(BotInstanceManagementService.class);
        User twitchUser = client.getHelix().getUsers(null, null, null).execute().getUsers().get(0);
        this.login = twitchUser.getLogin();
        this.client.getChat().joinChannel(this.login);
        this.id = twitchUser.getId();
        for (Module module : this.user.getModules()) {
            if (module.getModuleType().getCommandClass() == null)
                continue;
            this.modules.add((BaseModule) module.getModuleType().getCommandClass().getConstructor(Module.class, BotInstance.class).newInstance(module, this));
        }
        updateTokens();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (nextCheck >= System.currentTimeMillis())
                    updateTokens();
            }
        }, 0, 1000);
    }


    private void updateTokens() {
        if(user.spotifyTokens() == null) {
            botInstanceManagementService.stopInstance(user);
            return;
        }
        OAuth2Credential credential = new OAuth2Credential("twitch", user.twitchTokens().getAccessToken());
        credential.setRefreshToken(user.twitchTokens().getRefreshToken());
        Optional<OAuth2Credential> credentialOptional = oAuth2IdentityProvider.refreshCredential(credential);
        if (credentialOptional.isPresent()) {
            credential = credentialOptional.get();
            User twitchUser = client.getHelix().getUsers(credential.getAccessToken(), null, null).execute().getUsers().get(0);
            id = twitchUser.getId();
            login = twitchUser.getLogin();
            user.twitchTokens().setAccessToken(credential.getAccessToken());
            user.twitchTokens().setRefreshToken(credential.getRefreshToken());
            Logger.log(LogType.DEBUG, "Got new Twitch Token for " + login + " | " + credential.getAccessToken(), "Twitch", login, credential.getAccessToken());
        }
        try {
            AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            user.spotifyTokens().setAccessToken(authorizationCodeCredentials.getAccessToken());
            user.spotifyTokens().setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            Logger.log(LogType.DEBUG, "Got new Spotify Token for " + login + ", expires in: " + authorizationCodeCredentials.getExpiresIn() + " | " + authorizationCodeCredentials.getAccessToken().substring(0, 10) + "...", "Spotify", login, String.valueOf(authorizationCodeCredentials.getExpiresIn()), authorizationCodeCredentials.getAccessToken().substring(0, 10) + "...");
        } catch (BadRequestException e) {
            user.getTokenPairs().removeIf(tokenPair -> tokenPair.getTokenType().equals(TokenType.SPOTIFY));
            Logger.log(LogType.INFO, login + " revoked his access token so it was removed from the database.", login, "revoked", "database");
            botInstanceManagementService.stopInstance(user);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        nextCheck = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(50);
    }


}
