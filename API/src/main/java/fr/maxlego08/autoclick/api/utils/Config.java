package fr.maxlego08.autoclick.api.utils;

import fr.maxlego08.autoclick.api.ClickPlugin;
import fr.maxlego08.menu.api.requirement.Action;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {

    public static boolean debug = false;

    // Session
    public static int minimumDelay = 500;
    public static int sessionEndAfter = 50;
    public static int minimumSessionDuration = 60000;
    public static int minimumSessionClicks = 40;

    // Analyse
    public static double sessionTrimmed = 0.05;
    public static double standardDeviation = 20.0;
    public static int smallVariation = 10;
    public static int largeVariation = 150;
    public static double score = 60.0;
    public static double maxScore = 100.0;

    // Scoring détaillé
    public static double smallVariationThresholdPercent = 50.0;
    public static double smallVariationMultiplier = 0.5;
    public static double smallVariationMaxBonus = 25.0;

    public static double rangeRelativeThreshold = 0.3;
    public static double rangeMaxBonus = 25.0;

    public static double stddevRelativeThreshold = 0.1;
    public static double stddevMaxBonus = 20.0;

    public static double top1FrequencyThresholdPercent = 10.0;
    public static double top1FrequencyMultiplier = 0.5;

    public static double top3FrequencyThresholdPercent = 30.0;
    public static double top3FrequencyMultiplier = 0.3;

    public static double noLargeJumpBonus = 15.0;

    public static List<Action> endSessionActions = new ArrayList<>();
    public static List<Action> endCheatSessionActions = new ArrayList<>();

    public static SimpleDateFormat simpleDateFormat;
    public static String clickLoreLine;

    public static void load(FileConfiguration configuration, ClickPlugin plugin) {

        debug = configuration.getBoolean("debug", false);

        // Session
        minimumDelay = configuration.getInt("session.minimum-delay", 500);
        sessionEndAfter = configuration.getInt("session.end-after", 50);
        minimumSessionDuration = configuration.getInt("session.minimum-duration", 60000);
        minimumSessionClicks = configuration.getInt("session.minimum-clicks", 40);

        // Analyse
        sessionTrimmed = configuration.getDouble("analyze.session-trimmed", 0.05);
        standardDeviation = configuration.getDouble("analyze.standard-deviation", 20.0);
        smallVariation = configuration.getInt("analyze.small-variation", 10);
        largeVariation = configuration.getInt("analyze.large-variation", 150);
        score = configuration.getDouble("analyze.score", 60.0);
        maxScore = configuration.getDouble("analyze.max-score", 100.0);

        // Scoring
        smallVariationThresholdPercent = configuration.getDouble("analyze.scoring.small-variation.threshold-percent", 50.0);
        smallVariationMultiplier = configuration.getDouble("analyze.scoring.small-variation.multiplier", 0.5);
        smallVariationMaxBonus = configuration.getDouble("analyze.scoring.small-variation.max-bonus", 25.0);

        rangeRelativeThreshold = configuration.getDouble("analyze.scoring.range.relative-threshold", 0.3);
        rangeMaxBonus = configuration.getDouble("analyze.scoring.range.max-bonus", 25.0);

        stddevRelativeThreshold = configuration.getDouble("analyze.scoring.stddev.relative-threshold", 0.1);
        stddevMaxBonus = configuration.getDouble("analyze.scoring.stddev.max-bonus", 20.0);

        top1FrequencyThresholdPercent = configuration.getDouble("analyze.scoring.top1-frequency.threshold-percent", 10.0);
        top1FrequencyMultiplier = configuration.getDouble("analyze.scoring.top1-frequency.multiplier", 0.5);

        top3FrequencyThresholdPercent = configuration.getDouble("analyze.scoring.top3-frequency.threshold-percent", 30.0);
        top3FrequencyMultiplier = configuration.getDouble("analyze.scoring.top3-frequency.multiplier", 0.3);

        noLargeJumpBonus = configuration.getDouble("analyze.scoring.no-large-jump-bonus", 15.0);

        endSessionActions = plugin.getButtonManager().loadActions((List<Map<String, Object>>) configuration.getList("actions.end-session"), "end-session", new File(plugin.getDataFolder(), "config.yml"));
        endCheatSessionActions = plugin.getButtonManager().loadActions((List<Map<String, Object>>) configuration.getList("actions.end-cheat-session"), "end-cheat-session", new File(plugin.getDataFolder(), "config.yml"));

        simpleDateFormat = new SimpleDateFormat(configuration.getString("date-format", "dd/MM/yyyy HH:mm:ss"));
        clickLoreLine = configuration.getString("click-lore-line", "&f%click%ms");
    }
}

