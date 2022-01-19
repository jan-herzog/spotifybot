package de.notecho.spotify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@CookieValue(name = "session", defaultValue = "null") String session, Model model) {
        if (session.equals("null"))
            return "redirect:https://id.twitch.tv/oauth2/authorize?client_id=41cemr1kitmxxmv15o47v152b78xzr&redirect_uri=https://spitchbot.com/twitch/callback/&response_type=code&scope=user:read:email user_read";
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(name = "session", defaultValue = "null") String session, HttpServletResponse response, Model model) {
        if (session.equalsIgnoreCase("null"))
            return "redirect:/";
        Cookie cookie = new Cookie("session", session);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

}
