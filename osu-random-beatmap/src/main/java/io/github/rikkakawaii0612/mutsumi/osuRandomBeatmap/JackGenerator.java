package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JackGenerator implements Generator {
    private static final List<Integer> COLUMNS = List.of(0, 1, 2, 3);
    private final int startTime;
    private final double bpm;
    private final int[] config;
    private int line;

    private final Random random = new Random();

    public JackGenerator(int startTime, double bpm, int[] config) {
        this.startTime = startTime;
        this.bpm = bpm;
        this.config = config;
    }

    @Override
    public int getCurrentTime() {
        return this.startTime + (int) (15000.0D * this.line / this.bpm);
    }

    @Override
    public HitObject[] next() {
        int count = this.config[this.line % this.config.length];
        if (count < 0 || count > 4) {
            throw new IllegalArgumentException();
        }

        int time = this.getCurrentTime();
        this.line++;

        if (count == 0) {
            return new HitObject[0];
        }

        List<Integer> list = new ArrayList<>(COLUMNS);
        HitObject[] hitObjects = new HitObject[count];
        for (int i = 0; i < count; i++) {
            int column = list.remove(this.random.nextInt(list.size()));
            hitObjects[i] = new Circle(column, time);
        }

        return hitObjects;
    }
}
