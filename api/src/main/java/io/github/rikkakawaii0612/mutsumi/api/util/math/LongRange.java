package io.github.rikkakawaii0612.mutsumi.api.util.math;

public class LongRange {
    private final long min;
    private final long max;

    private LongRange(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public boolean matches(long value) {
        return this.min <= value && value <= this.max;
    }

    public static LongRange min(long min) {
        return new LongRange(min, Integer.MAX_VALUE);
    }

    public static LongRange max(long max) {
        return new LongRange(Integer.MIN_VALUE, max);
    }

    public static LongRange range(long min, long max) {
        return new LongRange(min, max);
    }

    public static LongRange positive() {
        return new LongRange(1L, Integer.MAX_VALUE);
    }

    public static LongRange unbounded() {
        return new LongRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
