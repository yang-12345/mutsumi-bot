package io.github.rikkakawaii0612.mutsumi.numberBomb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameInfo {
    private final int result;
    private final List<Long> players = new ArrayList<>();
    private int min;
    private int max;

    public GameInfo(int min, int max) {
        Random random = new Random();
        this.result = min + random.nextInt(max - min + 1);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getResult() {
        return result;
    }

    public boolean select(int i, long player) {
        // 玩家排队
        if (this.players.contains(player)) {
            this.players.remove(player);
            this.players.add(player);
        } else {
            this.players.addLast(player);
        }

        if (i == result) {
            return true;
        }

        if (i < result) {
            this.min = i + 1;
        } else {
            this.max = i - 1;
        }
        return false;
    }

    public List<Long> getPlayers() {
        return players;
    }
}
