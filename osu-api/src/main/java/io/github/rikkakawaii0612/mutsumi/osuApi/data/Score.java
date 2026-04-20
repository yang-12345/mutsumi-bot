package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Score implements ObjectData {
    @JsonProperty("id")
    public long id = 0L;

    @JsonProperty("beatmap_id")
    public long beatmapId = 0L;

    @JsonProperty("accuracy")
    public double accuracy = 0.0D;

    @JsonProperty("pp")
    public double pp;

    @JsonProperty("beatmap")
    public Beatmap beatmap;

    @JsonProperty("beatmapset")
    public Beatmapset beatmapset;

    public Score() {
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
        return "osuApi.Score(id=" + this.id + ")";
    }

    @Override
    public String getType() {
        return "osuApi.Score";
    }
}
