package de.notecho.spotify.web.controller;

import de.notecho.spotify.web.session.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginController {

    private final SessionManagementService sessionManagementService;

    @GetMapping("/login")
    public String login(@CookieValue(name = "session", defaultValue = "null") String session, Model model) {
        if(session.equals("null"))
            return "redirect:https://id.twitch.tv/oauth2/authorize?client_id=41cemr1kitmxxmv15o47v152b78xzr&redirect_uri=https://spitchbot.com/twitch/callback/&response_type=code&scope=user:read:email user_read";
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        return "redirect:/";
    }

}
