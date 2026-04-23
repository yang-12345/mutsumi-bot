package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeatmapPlayCount implements ObjectData {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

    @JsonProperty("beatmap_id")
    public long beatmapId = 0L;

    @JsonProperty("beatmap")
    public Beatmap beatmap;

    @JsonProperty("beatmapset")
    public Beatmapset beatmapset;

    @JsonProperty("count")
    public int count = 0;

    public BeatmapPlayCount() {
    }

    @Override
    public String toString() {
        return "osuApi.BeatmapPlayCount(beatmapId=" + this.beatmapId + ")";
    }

    @Override
    public ObjectData get(String key) {
        return switch (key) {
            case "beatmapId" -> ObjectData.of(this.beatmapId);
            case "beatmap" -> ObjectData.of(this.beatmap);
            case "beatmapset" -> ObjectData.of(this.beatmapset);
            case "count" -> ObjectData.of(this.count);
            default -> {
                LOGGER.warn("Trying to get unknown key '{}' of BeatmapPlayCount!", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    @Override
    public String getType() {
        return "osuApi.BeatmapPlayCount";
    }
}
