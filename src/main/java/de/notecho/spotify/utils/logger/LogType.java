package de.notecho.spotify.utils.logger;

import de.notecho.spotify.utils.ConsoleColors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LogType {
    ERROR(ConsoleColors.RED, ConsoleColors.RED_BOLD),
    INFO(ConsoleColors.RESET, ConsoleColors.YELLOW),
    DEBUG(ConsoleColors.RESET, ConsoleColors.RED),
    SUCCESS(ConsoleColors.GREEN, ConsoleColors.YELLOW),
    COMMANDRESPONSE(ConsoleColors.YELLOW, ConsoleColors.RED);

    @Getter
    private final String color;
    @Getter
    private final String highlightColor;
}
