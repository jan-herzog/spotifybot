package de.notecho.spotify.utils.logger;

import de.notecho.spotify.utils.ConsoleColors;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static String prefix = "SpotifyBot >>";
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private static final boolean debug = true;

    public static void log(LogType logType, String msg, String... highlights) {
        if (logType.equals(LogType.DEBUG))
            if (!debug)
                return;
        for (String highlight : highlights)
            msg = msg.replaceAll(highlight, logType.getHighlightColor() + highlight + logType.getColor());
        System.out.println(ConsoleColors.RESET +
                format.format(new Date()) +
                " " +
                ConsoleColors.CYAN +
                prefix +
                ConsoleColors.RESET +
                " - " +
                logType.getColor() +
                msg
        );
    }

}
