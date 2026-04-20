package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

public class BeatmapPlayCount implements ObjectData {
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
    public int asInt() {
        return Math.toIntExact(this.beatmapId);
    }

    @Override
    public long asLong() {
        return this.beatmapId;
    }

    @Override
    public double asDouble() {
        return this.beatmapId;
    }

    @Override
    public boolean asBoolean() {
        return this.beatmapId != 0L;
    }

    @Override
    public String asString() {
        return "osuApi.BeatmapPlayCount(beatmapId=" + this.beatmapId + ")";
    }

    @Override
    public String getType() {
        return "osuApi.BeatmapPlayCount";
    }
}
