package de.notecho.spotify.web.controller;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.repository.UserRepository;
import de.notecho.spotify.module.DefaultModules;
import de.notecho.spotify.web.session.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CallbackController {

    private final SessionManagementService sessionManagementService;

    private final TwitchClient twitchClient;

    private final OAuth2IdentityProvider oAuth2IdentityProvider;

    private final UserRepository repository;

    @GetMapping("/spotify/callback")
    public String spotifyCallback(Model model) {
        return "index";
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
            user = new BotUser(0L, twitchUser.getId(), DefaultModules.defaultList());
            repository.saveAndFlush(user);
        }
        sessionManagementService.createSession(user);
        return "redirect:/dashboard";
    }

}
