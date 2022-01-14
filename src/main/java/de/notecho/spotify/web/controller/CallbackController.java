package de.notecho.spotify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CallbackController {

    @GetMapping("/spotify/callback")
    public String spotifyCallback(Model model) {
        return "index";
    }

    @GetMapping("/spotify/unlink")
    public String spotifyUnlink(Model model) {
        return "index";
    }

    @GetMapping("/twitch/callback")
    public String twitchCallback(Model model) {
        return "index";
    }

}
