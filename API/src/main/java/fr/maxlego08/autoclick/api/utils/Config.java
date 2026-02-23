package fr.maxlego08.autoclick.api.utils;

import fr.maxlego08.autoclick.api.ClickPlugin;
import fr.maxlego08.menu.api.requirement.Action;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

        validateConfig(plugin.getLogger());
    }

    private static void validateConfig(Logger logger) {
        boolean hasErrors = false;

        // Session validation
        if (minimumDelay < 0) {
            logger.warning("session.minimum-delay doit être >= 0, valeur actuelle: " + minimumDelay + ". Réinitialisé à 500.");
            minimumDelay = 500;
            hasErrors = true;
        }

        if (sessionEndAfter <= 0) {
            logger.warning("session.end-after doit être > 0, valeur actuelle: " + sessionEndAfter + ". Réinitialisé à 50.");
            sessionEndAfter = 50;
            hasErrors = true;
        }

        if (minimumSessionDuration < 0) {
            logger.warning("session.minimum-duration doit être >= 0, valeur actuelle: " + minimumSessionDuration + ". Réinitialisé à 60000.");
            minimumSessionDuration = 60000;
            hasErrors = true;
        }

        if (minimumSessionClicks < 1) {
            logger.warning("session.minimum-clicks doit être >= 1, valeur actuelle: " + minimumSessionClicks + ". Réinitialisé à 40.");
            minimumSessionClicks = 40;
            hasErrors = true;
        }

        // Analyze validation
        if (sessionTrimmed < 0 || sessionTrimmed > 0.4) {
            logger.warning("analyze.session-trimmed doit être entre 0 et 0.4, valeur actuelle: " + sessionTrimmed + ". Réinitialisé à 0.05.");
            sessionTrimmed = 0.05;
            hasErrors = true;
        }

        if (standardDeviation < 0) {
            logger.warning("analyze.standard-deviation doit être >= 0, valeur actuelle: " + standardDeviation + ". Réinitialisé à 20.0.");
            standardDeviation = 20.0;
            hasErrors = true;
        }

        if (smallVariation <= 0) {
            logger.warning("analyze.small-variation doit être > 0, valeur actuelle: " + smallVariation + ". Réinitialisé à 10.");
            smallVariation = 10;
            hasErrors = true;
        }

        if (largeVariation <= smallVariation) {
            logger.warning("analyze.large-variation doit être > small-variation (" + smallVariation + "), valeur actuelle: " + largeVariation + ". Réinitialisé à 150.");
            largeVariation = 150;
            hasErrors = true;
        }

        if (maxScore <= 0) {
            logger.warning("analyze.max-score doit être > 0, valeur actuelle: " + maxScore + ". Réinitialisé à 100.0.");
            maxScore = 100.0;
            hasErrors = true;
        }

        if (score < 0 || score > maxScore) {
            logger.warning("analyze.score doit être entre 0 et " + maxScore + ", valeur actuelle: " + score + ". Réinitialisé à 60.0.");
            score = 60.0;
            hasErrors = true;
        }

        // Scoring validation
        if (smallVariationThresholdPercent < 0 || smallVariationThresholdPercent > 100) {
            logger.warning("analyze.scoring.small-variation.threshold-percent doit être entre 0 et 100, valeur actuelle: " + smallVariationThresholdPercent + ". Réinitialisé à 50.0.");
            smallVariationThresholdPercent = 50.0;
            hasErrors = true;
        }

        if (smallVariationMultiplier < 0) {
            logger.warning("analyze.scoring.small-variation.multiplier doit être >= 0, valeur actuelle: " + smallVariationMultiplier + ". Réinitialisé à 0.5.");
            smallVariationMultiplier = 0.5;
            hasErrors = true;
        }

        if (smallVariationMaxBonus < 0) {
            logger.warning("analyze.scoring.small-variation.max-bonus doit être >= 0, valeur actuelle: " + smallVariationMaxBonus + ". Réinitialisé à 25.0.");
            smallVariationMaxBonus = 25.0;
            hasErrors = true;
        }

        if (rangeRelativeThreshold < 0) {
            logger.warning("analyze.scoring.range.relative-threshold doit être >= 0, valeur actuelle: " + rangeRelativeThreshold + ". Réinitialisé à 0.3.");
            rangeRelativeThreshold = 0.3;
            hasErrors = true;
        }

        if (rangeMaxBonus < 0) {
            logger.warning("analyze.scoring.range.max-bonus doit être >= 0, valeur actuelle: " + rangeMaxBonus + ". Réinitialisé à 25.0.");
            rangeMaxBonus = 25.0;
            hasErrors = true;
        }

        if (stddevRelativeThreshold < 0) {
            logger.warning("analyze.scoring.stddev.relative-threshold doit être >= 0, valeur actuelle: " + stddevRelativeThreshold + ". Réinitialisé à 0.1.");
            stddevRelativeThreshold = 0.1;
            hasErrors = true;
        }

        if (stddevMaxBonus < 0) {
            logger.warning("analyze.scoring.stddev.max-bonus doit être >= 0, valeur actuelle: " + stddevMaxBonus + ". Réinitialisé à 20.0.");
            stddevMaxBonus = 20.0;
            hasErrors = true;
        }

        if (top1FrequencyThresholdPercent < 0 || top1FrequencyThresholdPercent > 100) {
            logger.warning("analyze.scoring.top1-frequency.threshold-percent doit être entre 0 et 100, valeur actuelle: " + top1FrequencyThresholdPercent + ". Réinitialisé à 10.0.");
            top1FrequencyThresholdPercent = 10.0;
            hasErrors = true;
        }

        if (top1FrequencyMultiplier < 0) {
            logger.warning("analyze.scoring.top1-frequency.multiplier doit être >= 0, valeur actuelle: " + top1FrequencyMultiplier + ". Réinitialisé à 0.5.");
            top1FrequencyMultiplier = 0.5;
            hasErrors = true;
        }

        if (top3FrequencyThresholdPercent < 0 || top3FrequencyThresholdPercent > 100) {
            logger.warning("analyze.scoring.top3-frequency.threshold-percent doit être entre 0 et 100, valeur actuelle: " + top3FrequencyThresholdPercent + ". Réinitialisé à 30.0.");
            top3FrequencyThresholdPercent = 30.0;
            hasErrors = true;
        }

        if (top3FrequencyMultiplier < 0) {
            logger.warning("analyze.scoring.top3-frequency.multiplier doit être >= 0, valeur actuelle: " + top3FrequencyMultiplier + ". Réinitialisé à 0.3.");
            top3FrequencyMultiplier = 0.3;
            hasErrors = true;
        }

        if (noLargeJumpBonus < 0) {
            logger.warning("analyze.scoring.no-large-jump-bonus doit être >= 0, valeur actuelle: " + noLargeJumpBonus + ". Réinitialisé à 15.0.");
            noLargeJumpBonus = 15.0;
            hasErrors = true;
        }

        if (hasErrors) {
            logger.warning("Des valeurs de configuration invalides ont été corrigées automatiquement.");
        }
    }
}

