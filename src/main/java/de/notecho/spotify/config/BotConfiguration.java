package de.notecho.spotify.config;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

@Configuration
public class BotConfiguration {

    @Value("${twitch.clientId}")
    private String twitchClientId;

    @Value("${twitch.clientSecret}")
    private String twitchClientSecret;

    @Value("${twitch.token}")
    private String twitchToken;

    @Bean
    public CredentialManager buildCredentialManager() {
        return CredentialManagerBuilder.builder().build();
    }

    @Bean
    @DependsOn("buildCredentialManager")
    public TwitchClient buildTwitchClient(CredentialManager credentialManager) {
        credentialManager.registerIdentityProvider(new TwitchIdentityProvider(twitchClientId, twitchClientSecret, "https://spitchbot.com/twitch/callback/"));
        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableKraken(true)
                .withEnablePubSub(true)
                .withCredentialManager(credentialManager)
                .withEnableChat(true)
                .withDefaultAuthToken(new OAuth2Credential("twitch", "oauth:" + twitchToken))
                .withChatAccount(new OAuth2Credential("twitch", "oauth:" + twitchToken))
                .build();
    }

    @Bean
    @DependsOn("buildCredentialManager")
    public OAuth2IdentityProvider getOAuth2IdentityProvider(CredentialManager credentialManager) {
        return credentialManager.getOAuth2IdentityProviderByName("twitch").get();
    }

    @Bean
    public SpotifyApi buildSpotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId("551e70c923a6485889ff1c8f0576571b")
                .setClientSecret("66fe49916ba94a58b6b695234ded6eb3")
                .setRedirectUri(SpotifyHttpManager.makeUri("https://spitchbot.com/spotify/callback"))
                .build();
    }

}
