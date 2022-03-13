package de.notecho.spotify.bot.modules.commands;

import de.notecho.spotify.bot.instance.BotInstance;
import de.notecho.spotify.bot.modules.Command;
import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.database.user.entities.module.ModuleEntry;
import de.notecho.spotify.module.ModuleType;
import de.notecho.spotify.module.UserLevel;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;

public class VolumeCommand extends Command {

    public VolumeCommand(Module module, BotInstance root) {
        super(module, root);
    }

    @SneakyThrows
    @Override
    public void exec(String userName, String id, UserLevel userLevel, String[] args) {
        if (args.length >= 1) {
            ModuleEntry setVolume = getModule().getEntry("setVolume");
            if (!userLevel.isHigherOrEquals(setVolume.getUserLevel())) {
                sendMessage(getModule(ModuleType.SYSTEM).getEntry("noPerms"), "$USER", userName, "$ROLE", setVolume.getUserLevel().getPrettyName());
                return;
            }
            String percent = args[0];
            int newPercent = 0;
            if (StringUtils.isNumeric(percent))
                newPercent = Integer.parseInt(percent);
            else if (percent.contains("-") || percent.contains("+")) {
                if (!StringUtils.isNumeric(percent.substring(1))) {
                    sendMessage(getModule(ModuleType.SYSTEM).getEntry("syntax"), "$USER", userName, "$USAGE", "!sVolume ((+/-)percent)");
                    return;
                }
                Integer currentVolume = getRoot().getSpotifyApi().getInformationAboutUsersCurrentPlayback().build().execute().getDevice().getVolume_percent();
                switch (percent.charAt(0)) {
                    case '+' -> newPercent = currentVolume + Integer.parseInt(percent.substring(1));
                    case '-' -> newPercent = currentVolume - Integer.parseInt(percent.substring(1));
                }
            } else {
                sendMessage(getModule(ModuleType.SYSTEM).getEntry("syntax"), "$USER", userName, "$USAGE", "!sVolume ((+/-)percent)");
                return;
            }
            getRoot().getSpotifyApi().setVolumeForUsersPlayback(newPercent).build().execute();
            sendMessage(setVolume, "$USER", userName, "$VOLUME", String.valueOf(newPercent));
            return;
        }
        ModuleEntry showVolume = getModule().getEntry("showVolume");
        if (!userLevel.isHigherOrEquals(showVolume.getUserLevel())) {
            sendMessage(getModule(ModuleType.SYSTEM).getEntry("noPerms"), "$USER", userName, "$ROLE", showVolume.getUserLevel().getPrettyName());
            return;
        }
        CurrentlyPlayingContext playingContext = getRoot().getSpotifyApi().getInformationAboutUsersCurrentPlayback().build().execute();
        sendMessage(showVolume, "$USER", userName, "$VOLUME", String.valueOf(playingContext.getDevice().getVolume_percent()));
    }

}
