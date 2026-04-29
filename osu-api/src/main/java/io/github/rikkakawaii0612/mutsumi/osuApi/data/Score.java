package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Score {
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
}
