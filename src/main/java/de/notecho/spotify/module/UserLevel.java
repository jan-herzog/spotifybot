package de.notecho.spotify.module;

import com.github.twitch4j.common.enums.CommandPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
public enum UserLevel {

    DEFAULT("Viewer"),
    VIP("Vip"),
    MOD("Moderator"),
    BROADCASTER("Broadcaster"),
    UNDEFINED("undefined");

    @Getter
    private final String prettyName;

    public boolean isHigherOrEquals(Set<CommandPermission> permissions) {
        if (this.equals(UserLevel.UNDEFINED))
            return true;
        if (permissions.contains(CommandPermission.BROADCASTER))
            return true;
        if (this.equals(UserLevel.VIP))
            return permissions.contains(CommandPermission.VIP);
        if (this.equals(UserLevel.MOD))
            return permissions.contains(CommandPermission.MODERATOR);
        if (this.equals(UserLevel.BROADCASTER))
            return false;
        return true;
    }

}
