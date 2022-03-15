package de.notecho.spotify.web.controller;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import de.notecho.spotify.config.BotConfiguration;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.entities.TokenPair;
import de.notecho.spotify.database.user.repository.TokenPairRepository;
import de.notecho.spotify.database.user.repository.UserRepository;
import de.notecho.spotify.module.TokenType;
import de.notecho.spotify.web.session.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AccountController {

    private final SessionManagementService sessionManagementService;

    private final BotConfiguration configuration;

    private final OAuth2IdentityProvider oAuth2IdentityProvider;

    private final UserRepository repository;

    private final TokenPairRepository tokenPairRepository;

    @GetMapping("/account/callback")
    public String twitchCallback(@CookieValue(name = "session", defaultValue = "null") String session, @RequestParam(name = "code", defaultValue = "null") String code) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return "redirect:/login";
        OAuth2Credential credentialByCode = oAuth2IdentityProvider.getCredentialByCode(code);
        user.addTokenPair(TokenPair.builder().accessToken(credentialByCode.getAccessToken()).refreshToken(credentialByCode.getRefreshToken()).tokenType(TokenType.CHATACCOUNT).build());
        repository.saveAndFlush(user);
        return "redirect:/dashboard";
    }

    @GetMapping("/account/unlink")
    public String accountUnlink(@CookieValue(name = "session", defaultValue = "null") String session) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return "redirect:/login";
        TokenPair tokenPair = user.chatAccountTokens();
        if (tokenPair == null)
            return "redirect:/dashboard";
        user.getTokenPairs().remove(tokenPair);
        repository.saveAndFlush(user);
        tokenPairRepository.delete(tokenPair);
        return "redirect:/dashboard";
    }

    @GetMapping("/account/link")
    public String accountLink(@CookieValue(name = "session", defaultValue = "null") String session) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return "redirect:/login";
        return "redirect:" + configuration.getTwitchAccountLink();
    }

}
