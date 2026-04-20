package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

public class BeatmapUserScore implements ObjectData {
    @JsonProperty("position")
    public int position = 0;

    @JsonProperty("score")
    public Score score;

    public BeatmapUserScore() {
    }

    @Override
    public int asInt() {
        return Math.toIntExact(this.position);
    }

    @Override
    public long asLong() {
        return this.position;
    }

    @Override
    public double asDouble() {
        return this.position;
    }

    @Override
    public boolean asBoolean() {
        return this.position != 0L;
    }

    @Override
    public String asString() {
        return "osuApi.BeatmapUserScore(#" + this.position + ", score=" + this.score + ")";
    }

    @Override
    public String getType() {
        return "osuApi.BeatmapUserScore";
    }
}
