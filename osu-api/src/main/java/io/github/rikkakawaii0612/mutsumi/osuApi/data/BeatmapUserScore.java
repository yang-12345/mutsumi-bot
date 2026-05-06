package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BeatmapUserScore {
    @JsonProperty("position")
    public int position = 0;

    @JsonProperty("score")
    public Score score;

    public BeatmapUserScore() {
    }

    @Override
    public String toString() {
        return "osuApi.BeatmapUserScore(#" + this.position + ", score=" + this.score + ")";
    }
}
