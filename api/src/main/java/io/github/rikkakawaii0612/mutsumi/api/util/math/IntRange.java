package io.github.rikkakawaii0612.mutsumi.api.util.math;

public class IntRange {
    private final int min;
    private final int max;

    private IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean matches(int value) {
        return this.min <= value && value <= this.max;
    }

    public static IntRange min(int min) {
        return new IntRange(min, Integer.MAX_VALUE);
    }

    public static IntRange max(int max) {
        return new IntRange(Integer.MIN_VALUE, max);
    }

    public static IntRange range(int min, int max) {
        return new IntRange(min, max);
    }

    public static IntRange positive() {
        return new IntRange(1, Integer.MAX_VALUE);
    }

    public static IntRange unbounded() {
        return new IntRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
