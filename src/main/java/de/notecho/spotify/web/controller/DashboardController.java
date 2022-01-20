package de.notecho.spotify.web.controller;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.web.session.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DashboardController {

    private final SessionManagementService sessionManagementService;

    private final TwitchClient twitchClient;

    @GetMapping("/dashboard")
    public String dashboard(@CookieValue(name = "session", defaultValue = "null") String session, Model model) {
        if(session.equals("null"))
            return "redirect:/login";
        BotUser user = sessionManagementService.getUser(session);
        List<User> twitchUsers = twitchClient.getHelix().getUsers(user.twitchTokens().getAccessToken(), null, null).execute().getUsers();
        User twitchUser = twitchUsers.get(0);
        model.addAttribute("username", twitchUser.getLogin());
        model.addAttribute("spotifyConnected", user.spotifyTokens() == null);
        return "dashboard";
    }

}
