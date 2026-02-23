package fr.maxlego08.autoclick;

import fr.maxlego08.autoclick.api.result.AnalyzeResult;
import fr.maxlego08.autoclick.api.utils.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickAnalyzer {

    public static AnalyzeResult analyzeSession(List<Integer> clicks) {
        if (clicks == null || clicks.size() < 10) {
            return AnalyzeResult.empty();
        }

        List<Integer> sorted = new ArrayList<>(clicks);
        Collections.sort(sorted);

        int removeCount = (int) (sorted.size() * Config.sessionTrimmed);
        if (removeCount * 2 >= sorted.size()) {
            return AnalyzeResult.empty();
        }

        List<Integer> cleaned = sorted.subList(removeCount, sorted.size() - removeCount);
        int size = cleaned.size();

        // === PASSE UNIQUE : calcul de sum, min, max, frequencyMap, variations ===
        long sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        Map<Integer, Integer> frequencyMap = new HashMap<>();

        for (int click : cleaned) {
            sum += click;
            if (click < min) min = click;
            if (click > max) max = click;
            frequencyMap.merge(click, 1, Integer::sum);
        }

        double mean = (double) sum / size;
        int range = max - min;

        // === SECONDE PASSE : variance et détection des variations ===
        double varianceSum = 0;
        int smallVariations = 0;
        int largeJumpDetected = 0;
        int last = cleaned.get(0);

        for (int i = 0; i < size; i++) {
            int click = cleaned.get(i);
            varianceSum += (click - mean) * (click - mean);

            if (i > 0) {
                int diff = Math.abs(click - last);
                if (diff < Config.smallVariation) smallVariations++;
                if (diff > Config.largeVariation) largeJumpDetected++;
            }
            last = click;
        }

        double variance = varianceSum / size;
        double stdDev = Math.sqrt(variance);
        double smallVariationPercent = (smallVariations * 100.0) / (size - 1);

        // === Tri des fréquences (sur une petite map, pas sur toute la liste) ===
        List<Map.Entry<Integer, Integer>> topFrequencies = new ArrayList<>(frequencyMap.entrySet());
        topFrequencies.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        double top1Percentage = (topFrequencies.get(0).getValue() * 100.0) / size;

        int top3Sum = 0;
        int limit = Math.min(3, topFrequencies.size());
        for (int i = 0; i < limit; i++) {
            top3Sum += topFrequencies.get(i).getValue();
        }
        double top3Percentage = (top3Sum * 100.0) / size;

        // === Calcul du score ===
        double score = 0.0;

        // Small variations
        if (smallVariationPercent > Config.smallVariationThresholdPercent) {
            score += (smallVariationPercent - Config.smallVariationThresholdPercent) * Config.smallVariationMultiplier;
        }

        // Range
        if (range < mean * Config.rangeRelativeThreshold) {
            score += (1 - (range / (mean * Config.rangeRelativeThreshold))) * Config.rangeMaxBonus;
        }

        // Standard deviation
        if (stdDev < mean * Config.stddevRelativeThreshold) {
            score += (1 - (stdDev / (mean * Config.stddevRelativeThreshold))) * Config.stddevMaxBonus;
        }

        // Top 1 frequency
        if (top1Percentage > Config.top1FrequencyThresholdPercent) {
            score += (top1Percentage - Config.top1FrequencyThresholdPercent) * Config.top1FrequencyMultiplier;
        }

        // Top 3 frequencies
        if (top3Percentage > Config.top3FrequencyThresholdPercent) {
            score += (top3Percentage - Config.top3FrequencyThresholdPercent) * Config.top3FrequencyMultiplier;
        }

        // No large jumps
        if (largeJumpDetected == 0) {
            score += Config.noLargeJumpBonus;
        }

        score = Math.min(score, Config.maxScore);
        return new AnalyzeResult(score >= Config.score, score);
    }
}
