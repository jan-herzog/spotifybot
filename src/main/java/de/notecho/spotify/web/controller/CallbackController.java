package de.notecho.spotify.web.controller;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.notecho.spotify.bot.BotInstanceManagementService;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.entities.TokenPair;
import de.notecho.spotify.database.user.repository.UserRepository;
import de.notecho.spotify.module.DefaultModules;
import de.notecho.spotify.module.TokenType;
import de.notecho.spotify.web.session.SessionManagementService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CallbackController {

    private final SessionManagementService sessionManagementService;

    private final TwitchClient twitchClient;

    private final SpotifyApi spotifyApi;

    private final OAuth2IdentityProvider oAuth2IdentityProvider;

    private final BotInstanceManagementService botInstanceManagementService;

    private final UserRepository repository;

    @SneakyThrows
    @GetMapping("/spotify/callback")
    public String spotifyCallback(@RequestParam(name = "code", defaultValue = "null") String code, @CookieValue(name = "session", defaultValue = "null") String session, Model model) {
        if (code.equals("null"))
            return "redirect:/erorr?code=503";
        if (sessionManagementService.getUser(session) == null)
            return "redirect:/erorr?code=503";
        BotUser user = sessionManagementService.getUser(session);
        AuthorizationCodeCredentials codeCredentials = spotifyApi.authorizationCode(code).build().execute();
        if (user.spotifyTokens() != null) {
            user.spotifyTokens().setAccessToken(codeCredentials.getAccessToken());
            user.spotifyTokens().setRefreshToken(codeCredentials.getRefreshToken());
        } else
            user.getTokenPairs().add(new TokenPair(0L, codeCredentials.getAccessToken(), codeCredentials.getRefreshToken(), TokenType.SPOTIFY));
        repository.saveAndFlush(user);
        botInstanceManagementService.startInstance(user);
        return "redirect:/dashboard";
    }

    @GetMapping("/spotify/unlink")
    public String spotifyUnlink(Model model) {
        return "index";
    }

    @GetMapping("/twitch/callback")
    public String twitchCallback(@RequestParam(name = "code", defaultValue = "null") String code, HttpServletResponse response, Model model) {
        if (code.equals("null"))
            return "redirect:/erorr?code=503";
        OAuth2Credential credentialByCode = oAuth2IdentityProvider.getCredentialByCode(code);
        List<User> users = twitchClient.getHelix().getUsers(credentialByCode.getAccessToken(), null, null).execute().getUsers();
        if (users.size() == 0)
            return "redirect:/erorr?code=503";
        User twitchUser = users.get(0);
        BotUser user = repository.findByTwitchId(twitchUser.getId());
        if (user == null) {
            user = new BotUser(0L, twitchUser.getId(), DefaultModules.defaultList(), Collections.singletonList(new TokenPair(0L, credentialByCode.getAccessToken(), credentialByCode.getRefreshToken(), TokenType.TWITCH)));
        } else {
            user.twitchTokens().setAccessToken(credentialByCode.getAccessToken());
            user.twitchTokens().setRefreshToken(credentialByCode.getRefreshToken());
        }
        repository.saveAndFlush(user);
        Cookie cookie = new Cookie("session", sessionManagementService.createSession(user));
        cookie.setMaxAge(3600);
        return "redirect:/dashboard";
    }

}
