package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Score implements ObjectData {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

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
    public String toString() {
        return "osuApi.Score(id=" + this.id + ")";
    }

    @Override
    public ObjectData get(String key) {
        return switch (key) {
            case "id" -> ObjectData.of(this.id);
            case "beatmapId" -> ObjectData.of(this.beatmapId);
            case "accuracy" -> ObjectData.of(this.accuracy);
            case "pp" -> ObjectData.of(this.pp);
            case "beatmap" -> ObjectData.of(this.beatmap);
            case "beatmapset" -> ObjectData.of(this.beatmapset);
            default -> {
                LOGGER.warn("Trying to get unknown key '{}' of Score!", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    @Override
    public String getType() {
        return "osuApi.Score";
    }
}
