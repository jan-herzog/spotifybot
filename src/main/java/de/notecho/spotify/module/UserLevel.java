package de.notecho.spotify.module;

import com.github.twitch4j.common.enums.CommandPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
public enum UserLevel {

    DEFAULT("Viewer", 0),
    VIP("Vip", 1),
    MOD("Moderator", 2),
    BROADCASTER("Broadcaster", 3);


    @Getter
    private final String prettyName;

    @Getter
    private final int power;

    public static UserLevel get(Set<CommandPermission> permissions) {
        if(permissions.contains(CommandPermission.BROADCASTER))
            return BROADCASTER;
        if(permissions.contains(CommandPermission.MODERATOR))
            return MOD;
        if(permissions.contains(CommandPermission.VIP))
            return VIP;
        return DEFAULT;
    }

    public boolean isHigherOrEquals(UserLevel userLevel) {
        return this.power >= userLevel.power;
    }

}
