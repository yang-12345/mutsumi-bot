package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeatmapUserScore implements ObjectData {
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

    @Override
    public ObjectData get(String key) {
        return switch (key) {
            case "position" -> ObjectData.of(this.position);
            case "score" -> ObjectData.of(this.score);
            default -> {
                LOGGER.warn("Trying to get unknown key '{}' of BeatmapUserScore!", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    @Override
    public String getType() {
        return "osuApi.BeatmapUserScore";
    }
}
