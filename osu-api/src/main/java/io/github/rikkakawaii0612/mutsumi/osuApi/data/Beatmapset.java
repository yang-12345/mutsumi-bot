package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.*;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beatmapset implements ObjectData {
    @JsonProperty("id")
    public long id = 0L;

    @JsonProperty("artist")
    public String artist = "";

    @JsonProperty("artist_unicode")
    public String artistUnicode = "";

    @JsonProperty("title")
    public String title = "";

    @JsonProperty("title_unicode")
    public String titleUnicode = "";

    @JsonProperty("creator")
    public String creator = "";

    @JsonProperty("beatmaps")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public List<Beatmap> beatmaps;

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
        return "osuApi.Beatmapset(id=" + this.id + ")";
    }

    @Override
    public String getType() {
        return "osuApi.Beatmapset";
    }
}
