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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ModuleController {

    private final SessionManagementService sessionManagementService;

    private final BotInstanceManagementService botInstanceManagementService;

    private final UserRepository userRepository;

    @PostMapping(value = "/modules/{moduleType}/update", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> updateModule(@PathVariable String moduleType, @CookieValue(name = "session", defaultValue = "null") String session, @RequestBody MultiValueMap<String, String> paramMap) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return ResponseEntity.badRequest().build();
        Module module = user.getModules().stream().filter(m -> m.getModuleType().equals(ModuleType.valueOf(moduleType.toUpperCase()))).findAny().orElse(null);
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

    @GetMapping(value = "/modules/{moduleType}/disable")
    public ResponseEntity<String> disableModule(@PathVariable String moduleType, @CookieValue(name = "session", defaultValue = "null") String session) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return ResponseEntity.badRequest().build();
        Module module = user.getModules().stream().filter(m -> m.getModuleType().equals(ModuleType.valueOf(moduleType))).findAny().orElse(null);
        if (module == null)
            return ResponseEntity.badRequest().build();
        module.disable();
        userRepository.saveAndFlush(user);
        botInstanceManagementService.updateModules(user);
        return ResponseEntity.ok("Success: " + user.getId());
    }


    @GetMapping(value = "/modules/{moduleType}/enable")
    public ResponseEntity<String> enableModule(@PathVariable String moduleType, @CookieValue(name = "session", defaultValue = "null") String session) {
        BotUser user = sessionManagementService.getUser(session);
        if (session.equals("null") || user == null)
            return ResponseEntity.badRequest().build();
        Module module = user.getModules().stream().filter(m -> m.getModuleType().equals(ModuleType.valueOf(moduleType))).findAny().orElse(null);
        if (module == null)
            return ResponseEntity.badRequest().build();
        module.enable();
        userRepository.saveAndFlush(user);
        botInstanceManagementService.updateModules(user);
        return ResponseEntity.ok("Success: " + user.getId());
    }


}
