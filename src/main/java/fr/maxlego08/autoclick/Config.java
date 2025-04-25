package fr.maxlego08.autoclick;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static int minimumDelay;
    public static int sessionEndAfter;
    public static boolean debug;

    public static void load(FileConfiguration configuration) {

        debug = configuration.getBoolean("debug", false);
        minimumDelay = configuration.getInt("minimum-delay", 500);
        sessionEndAfter = configuration.getInt("session-end-after", 5000);
    }

}
