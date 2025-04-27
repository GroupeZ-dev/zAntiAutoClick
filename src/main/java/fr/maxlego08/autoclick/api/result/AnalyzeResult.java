package fr.maxlego08.autoclick.api.result;

public record AnalyzeResult(boolean isCheat, double percent) {

    public static AnalyzeResult empty() {
        return new AnalyzeResult(false, 0);
    }

}
