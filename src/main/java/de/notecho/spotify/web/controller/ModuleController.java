package de.notecho.spotify.web.controller;

import de.notecho.spotify.bot.BotInstanceManagementService;
import de.notecho.spotify.database.user.entities.BotUser;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
import de.notecho.spotify.database.user.repository.UserRepository;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.web.session.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ModuleController {

    private final SessionManagementService sessionManagementService;

    private final BotInstanceManagementService botInstanceManagementService;

    private final UserRepository userRepository;

    @PostMapping(value = "/modules/{{moduleType}}", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> updateModule(@PathVariable String moduleType, @CookieValue(name = "session", defaultValue = "null") String session, MultiValueMap<String, String> paramMap) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return ResponseEntity.badRequest().build();
        Module module = user.getModules().stream().filter(m -> m.getModuleType().equals(ModuleType.valueOf(moduleType))).findAny().orElse(null);
        if (module == null)
            return ResponseEntity.badRequest().build();
        paramMap.forEach((k, v) -> {
            ModuleEntry entry = module.getEntry(k);
            if (entry == null)
                return;
            entry.setEntryValue(v.get(0));
        });
        userRepository.saveAndFlush(user);
        botInstanceManagementService.updateModules(user);
        return ResponseEntity.ok("Success: " + user.getId());
    }

}
