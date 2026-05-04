package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap.beatmap;

import io.github.rikkakawaii0612.mutsumi.api.util.math.MathUtils;

import java.util.*;

public class JackGenerator extends Generator {
    private static final List<Integer> COLUMNS = List.of(0, 1, 2, 3);
    protected int[] config = {3, 2, 2, 2};
    protected double anchor = 0.0D;
    private int line;
    private final int[] anchored = {0, 0, 0, 0}; // 用于记录每个 note 当前的锚长

    private final Random random = new Random();

    public JackGenerator() {
    }

    @Override
    public void setParameter(String key, String value) {
        switch (key.toLowerCase(Locale.ROOT)) {
            case "config" -> {
                if (value.isBlank()) {
                    throw new IllegalArgumentException("啥都没有，打集贸呢？");
                }

                if (!value.matches("[01234]+")) {
                    throw new IllegalArgumentException("请输入正确的密度配置，应为 0, 1, 2, 3, 4 组成的序列。");
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

            case "anchor" -> {
                try {
                    double anchor = Double.parseDouble(value);
                    if (!Double.isFinite(anchor)) {
                        throw new IllegalArgumentException("……锚度起码得是个有限值吧？");
                    }
                    if (anchor < 0.0D) {
                        throw new IllegalArgumentException("……还有比不锚还要不锚的东西吗。");
                    }
                    if (anchor > 5.0D) {
                        throw new IllegalArgumentException("锚上天了你这是？");
                    }
                    this.anchor = anchor;
                } catch (NumberFormatException _) {
                    throw new IllegalArgumentException("锚度必须是个 [0,5] 内的数字。");
                }
            }

            default -> super.setParameter(key, value);
        }
    }

    @Override
    public List<String> getInfo() {
        List<String> list = super.getInfo();
        list.add("锚度 (anchor)：" + this.anchor);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.config.length; i++) {
            builder.append(this.config[i]);
            if (i % 4 == 3) {
                builder.append(' ');
            }
        }
        list.add("配置 (config)：" + builder);
        return list;
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
        List<Double> weights = new ArrayList<>();
        if (this.anchor <= 1.0D) {
            double p = (this.anchor - 1.0D) / (this.anchor + 1.0D);
            for (int i : this.anchored) {
                weights.add(Math.exp(p * Math.log(0.5D + i))); // w = x ^ ( (a-1) / (a+1) )
            }
        } else {
            double p = 1.0D + (this.anchor - 1.0D) / 8.0D;
            for (int i : this.anchored) {
                // p = 1 + (a-1)/8
                // w = e ^ ( (1-p) * (0.5sqrt(x) - 0.5 - p)^2 )
                double d = 0.5D * Math.sqrt(i) - 0.5D - p;
                weights.add(Math.exp((1.0D - p) * d * d));
            }
        }
        HitObject[] hitObjects = new HitObject[count];
        for (int i = 0; i < count; i++) {
            int index = list.indexOf(MathUtils.choose(list, weights));
            int column = list.remove(index);
            weights.remove(index);
            hitObjects[i] = new Circle(column, time);
            this.anchored[column]++;
        }
        list.forEach(i -> this.anchored[i] = 0); // 有空格的列会清空标记

        return hitObjects;
    }

    @Override
    public String getName() {
        return "叠（Jack）";
    }
}
