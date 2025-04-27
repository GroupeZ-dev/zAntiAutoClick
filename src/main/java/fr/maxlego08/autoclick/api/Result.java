package fr.maxlego08.autoclick.api;

public record Result(boolean isCheat, double percent) {

    public static Result empty() {
        return new Result(false, 0);
    }

}
