package fr.maxlego08.autoclick;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static int minimumDelay;
    public static int sessionEndAfter;

    public static void load(FileConfiguration configuration) {

        minimumDelay = configuration.getInt("minimum-delay", 500);
        sessionEndAfter = configuration.getInt("session-end-after", 5000);
    }

}
