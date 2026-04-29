package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeatmapPlayCount {
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
}
