package de.notecho.spotify.bot.modules.commands;

import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.bot.modules.Command;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.module.UserLevel;
import lombok.SneakyThrows;

public class PauseCommand extends Command {

    public PauseCommand(Module module, BotInstance root) {
        super(module, root);
    }

    @SneakyThrows
    @Override
    public void exec(String userName, String id, UserLevel userLevel, String[] args) {
        ModuleEntry sPause = getModule().getEntry("sPause");
        if (!userLevel.isHigherOrEquals(sPause.getUserLevel())) {
            sendMessage(getModule(ModuleType.SYSTEM).getEntry("noPerms"), "$USER", userName, "$ROLE", sPause.getUserLevel().getPrettyName());
            return;
        }
        getRoot().getSpotifyApi().pauseUsersPlayback().build().execute();
        sendMessage(sPause, "$USER", userName);
    }
}
