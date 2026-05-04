package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

public record Circle(int column, int time, boolean start) implements HitObject {
    public Circle(int column, int time) {
        this(column, time, false);
    }

    @Override
    public int getTime() {
        return this.time;
    }

    @Override
    public String toSyntax() {
        return String.format("%d,192,%d,%d,0,0:0:0:0:",
                64 + 128 * this.column,
                this.time,
                this.start ? 5 : 1);
    }
}
