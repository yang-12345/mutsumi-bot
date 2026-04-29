package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeatmapUserScore {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

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
