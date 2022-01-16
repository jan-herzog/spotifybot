package de.notecho.spotify.database.user.entities.module;

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

}
