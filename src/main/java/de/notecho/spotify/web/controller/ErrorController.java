package de.notecho.spotify.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String onError(@RequestParam(name = "code", defaultValue = "null") String code, Model model) {
        if(!code.equals("null"))
            model.addAttribute("code", code);
        return "error";
    }

}
