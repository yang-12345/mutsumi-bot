package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap.beatmap;

import java.util.*;

public class SpeedGenerator extends Generator {
    private double bumpChance;
    private int[] config = {2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1};
    private int line;
    private final int[] cooldowns = {0, 0, 0, 0};

    private final Random random = new Random();

    public SpeedGenerator() {
    }

    @Override
    public void setParameter(String key, String value) {
        switch (key.toLowerCase(Locale.ROOT)) {
            case "config" -> {
                if (value.isBlank()) {
                    throw new IllegalArgumentException("啥都没有，打集贸呢？");
                }

                if (!value.matches("[012]+")) {
                    throw new IllegalArgumentException("请输入正确的密度配置，应为 0, 1, 2 组成的序列。");
                }

                if (value.matches("0+")) {
                    throw new IllegalArgumentException("全是 0，打集贸呢？");
                }

                int len = value.length();
                this.config = new int[len];
                for (int i = 0; i < len; i++) {
                    this.config[i] = Character.getNumericValue(value.charAt(i));
                }
            }

            case "bump" -> {
                try {
                    double bump = Double.parseDouble(value);
                    if (!Double.isFinite(bump)) {
                        throw new IllegalArgumentException("……子弹率起码得是个有限值吧？");
                    }
                    if (bump < 0.0D) {
                        throw new IllegalArgumentException("……这都没子弹了还要再删点子弹吗？");
                    }
                    if (bump > 1.0D) {
                        throw new IllegalArgumentException("你这是想写乱还是写切？");
                    }
                    this.bumpChance = bump;
                } catch (NumberFormatException _) {
                    throw new IllegalArgumentException("子弹率必须是个 [0,1] 内的数字。");
                }
            }

            default -> super.setParameter(key, value);
        }
    }

    @Override
    public List<String> getInfo() {
        List<String> list = super.getInfo();
        list.add("子弹率 (bump)：" + this.bumpChance);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.config.length; i++) {
            builder.append(this.config[i]);
            if (i % 8 == 7) {
                builder.append(' ');
            }
        }
        list.add("配置 (config)：" + builder);
        return list;
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
            boolean bl = bumps.isEmpty();
            if (!normals.isEmpty() && (bl || this.random.nextDouble() >= this.bumpChance)) {
                column = normals.remove(this.random.nextInt(normals.size()));
            } else if (!bl) {
                column = bumps.remove(this.random.nextInt(bumps.size()));
            } else {
                continue;
            }
            hitObjects.add(new Circle(column, time));
            this.cooldowns[column] = 2;
        }

        return hitObjects.toArray(new HitObject[0]);
    }

    @Override
    public String getName() {
        return "乱（Speed）";
    }
}
