package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.*;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beatmap implements ObjectData {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

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
    public String toString() {
        return "osuApi.Beatmap(id=" + this.id + ")";
    }

    @Override
    public ObjectData get(String key) {
        return switch (key) {
            case "id" -> ObjectData.of(this.id);
            case "rating" -> ObjectData.of(this.rating);
            case "playMode" -> ObjectData.of(this.playMode.getName());
            case "version" -> ObjectData.of(this.version);
            case "rankStatus" -> ObjectData.of(this.rankStatus.getName());
            case "beatmapsetId" -> ObjectData.of(this.beatmapsetId);
            case "cs" -> ObjectData.of(this.cs);
            case "beatmapset" -> ObjectData.of(this.beatmapset);
            default -> {
                LOGGER.warn("Trying to get unknown key '{}' of Beatmap!", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    @Override
    public String getType() {
        return "osuApi.Beatmap";
    }
}
