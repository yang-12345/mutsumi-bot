package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.*;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beatmap implements ObjectData {
    @JsonProperty("id")
    public long id = 0L;

    @JsonProperty("difficulty_rating")
    public double rating = 0.0D;

    @JsonProperty("mode")
    public PlayMode playMode = PlayMode.STANDARD;

    @JsonProperty("version")
    public String version = "";

    @JsonProperty("status")
    public RankStatus rankStatus = RankStatus.GRAVEYARD;

    @JsonProperty("beatmapset_id")
    public long beatmapsetId = 0L;

    @JsonProperty("cs")
    public double cs = 0.0D;

    @JsonProperty("beatmapset")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public Beatmapset beatmapset;

    public Beatmap() {
    }

    @Override
    public int asInt() {
        return Math.toIntExact(this.id);
    }

    @Override
    public long asLong() {
        return this.id;
    }

    @Override
    public double asDouble() {
        return this.id;
    }

    @Override
    public boolean asBoolean() {
        return this.id != 0L;
    }

    @Override
    public String asString() {
        return "osuApi.Beatmap(id=" + this.id + ")";
    }

    @Override
    public String getType() {
        return "osuApi.Beatmap";
    }
}
