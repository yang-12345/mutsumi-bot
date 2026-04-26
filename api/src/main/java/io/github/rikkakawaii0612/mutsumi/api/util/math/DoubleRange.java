package io.github.rikkakawaii0612.mutsumi.api.util.math;

public class DoubleRange {
    private final double min;
    private final double max;

    private DoubleRange(double min, double max) {
        this.min = min;
        this.max = max;
    }
    
    public boolean matches(double value) {
        return this.min <= value && value <= this.max;
    }

    public static DoubleRange min(double min) {
        return new DoubleRange(min, Double.POSITIVE_INFINITY);
    }

    public static DoubleRange minExclusive(double min) {
        return new DoubleRange(Math.nextUp(min), Double.POSITIVE_INFINITY);
    }

    public static DoubleRange max(double max) {
        return new DoubleRange(Double.NEGATIVE_INFINITY, max);
    }

    public static DoubleRange maxExclusive(double max) {
        return new DoubleRange(Double.NEGATIVE_INFINITY, Math.nextDown(max));
    }

    public static DoubleRange range(double min, double max) {
        return new DoubleRange(min, max);
    }

    public static DoubleRange rangeExclusive(double min, double max) {
        return new DoubleRange(Math.nextUp(min), Math.nextDown(max));
    }

    public static DoubleRange rangeMinExclusive(double min, double max) {
        return new DoubleRange(Math.nextUp(min), max);
    }

    public static DoubleRange rangeMaxExclusive(double min, double max) {
        return new DoubleRange(min, Math.nextDown(max));
    }

    public static DoubleRange positive() {
        return new DoubleRange(Double.MIN_VALUE, Double.POSITIVE_INFINITY);
    }

    public static DoubleRange unbounded() {
        return new DoubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public static DoubleRange unboundedExclusive() {
        return new DoubleRange(-Double.MAX_VALUE, Double.MAX_VALUE);
    }
}
