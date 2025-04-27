package fr.maxlego08.autoclick.zcore.utils;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static boolean debug = false;

    // Session
    public static int minimumDelay = 500;
    public static int sessionEndAfter = 5000;
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

    public static void load(FileConfiguration configuration) {

        debug = configuration.getBoolean("debug", false);

        // Session
        minimumDelay = configuration.getInt("session.minimum-delay", 500);
        sessionEndAfter = configuration.getInt("session.session-end-after", 5000);
        minimumSessionDuration = configuration.getInt("session.minimum-session-duration", 60000);
        minimumSessionClicks = configuration.getInt("session.minimum-session-clicks", 40);

        // Analyse
        sessionTrimmed = configuration.getDouble("analyze.session-trimmed", 0.05);
        standardDeviation = configuration.getDouble("analyze.standard-deviation", 20.0);
        smallVariation = configuration.getInt("analyze.small-variation", 10);
        largeVariation = configuration.getInt("analyze.large-variation", 150);
        score = configuration.getDouble("analyze.score", 60.0);
        maxScore = configuration.getDouble("analyze.max-score", 100.0);

        // Scoring détaillé
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
    }
}

