package fr.maxlego08.autoclick;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static int minimumDelay;
    public static int sessionEndAfter;
    public static int minimumSessionDuration;
    public static int minimumSessionClicks;
    public static double removePercent;
    public static boolean debug;
    public static double sessionIsNormal;

    public static void load(FileConfiguration configuration) {

        debug = configuration.getBoolean("debug", false);
        minimumDelay = configuration.getInt("minimum-delay", 500);
        sessionEndAfter = configuration.getInt("session-end-after", 5000);
        minimumSessionDuration = configuration.getInt("minimum-session-duration", 60000);
        minimumSessionClicks = configuration.getInt("minimum-session-clicks", 40);
        removePercent = configuration.getDouble("remove-percent", 0.05);
        sessionIsNormal = configuration.getDouble("session-is-normal", 20.0);
    }

}
