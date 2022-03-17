package de.notecho.spotify.web.controller;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
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
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return "redirect:/login";
        List<User> twitchUsers = twitchClient.getHelix().getUsers(user.twitchTokens().getAccessToken(), null, null).execute().getUsers();
        User twitchUser = twitchUsers.get(0);
        model.addAttribute("username", twitchUser.getLogin());
        model.addAttribute("spotifyConnected", user.spotifyTokens() != null);

        if (user.chatAccountTokens() != null) {
            List<User> chatUsers = twitchClient.getHelix().getUsers(user.chatAccountTokens().getAccessToken(), null, null).execute().getUsers();
            User chatUser = chatUsers.get(0);
            model.addAttribute("botUsername", chatUser.getLogin());
        } else model.addAttribute("botUsername", "null");
        for (Module module : user.getModules())
            for (ModuleEntry entry : module.getEntries())
                model.addAttribute(entry.getEntryKey(), entry.getEntryValue());
        return "dashboard";
    }

}
