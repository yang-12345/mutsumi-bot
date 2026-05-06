package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BeatmapPlayCount {
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
