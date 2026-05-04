package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpeedGenerator implements Generator {
    private final int startTime;
    private final double bpm;
    private final double bumpChance;
    private final int[] config;
    private int line;
    private final int[] cooldowns = {0, 0, 0, 0};

    private final Random random = new Random();

    public SpeedGenerator(int startTime, double bpm, double bumpChance, int[] config) {
        this.startTime = startTime;
        this.bpm = bpm;
        this.bumpChance = bumpChance;
        this.config = config;
    }

    @Override
    public int getCurrentTime() {
        return this.startTime + (int) (7500.0D * this.line / this.bpm);
    }

    @Override
    public HitObject[] next() {
        int count = this.config[this.line % this.config.length];
        if (count < 0 || count > 3) {
            throw new IllegalArgumentException();
        }

        int time = this.getCurrentTime();
        this.line++;

        List<Integer> normals = new ArrayList<>();
        List<Integer> bumps = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (this.cooldowns[i] == 0) {
                normals.add(i);
            } else {
                if (this.cooldowns[i] == 1) {
                    bumps.add(i);
                }
                this.cooldowns[i]--;
            }
        }

        List<HitObject> hitObjects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int column;
            if (!normals.isEmpty() && this.random.nextDouble() >= this.bumpChance) {
                column = normals.remove(this.random.nextInt(normals.size()));
            } else if (!bumps.isEmpty()) {
                column = bumps.remove(this.random.nextInt(bumps.size()));
            } else {
                continue;
            }
            hitObjects.add(new Circle(column, time));
            this.cooldowns[column] = 2;
        }

        return hitObjects.toArray(new HitObject[0]);
    }
}
