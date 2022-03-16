package de.notecho.spotify.web.controller;

import de.notecho.spotify.config.BotConfiguration;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.web.session.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginController {

    private final SessionManagementService sessionManagementService;

    private final BotConfiguration configuration;

    @GetMapping("/login")
    public String login(@CookieValue(name = "session", defaultValue = "null") String session, @RequestParam("force_verify") boolean forceVerify, Model model) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return "redirect:" + configuration.getTwitchLink() + (forceVerify ? "&force_verify=true" : "");
        return "redirect:/dashboard";
    }

    @GetMapping("/spotify/login")
    public String login(@CookieValue(name = "session", defaultValue = "null") String session) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return "redirect:/login";
        return "redirect:" + configuration.getSpotifyLink();
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(name = "session", defaultValue = "null") String session, HttpServletResponse response, Model model) {
        if (session.equalsIgnoreCase("null"))
            return "redirect:/";
        Cookie cookie = new Cookie("session", session);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

}
