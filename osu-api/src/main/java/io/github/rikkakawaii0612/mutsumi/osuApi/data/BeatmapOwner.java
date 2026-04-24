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
public class BeatmapOwner implements ObjectData {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

    @JsonProperty("id")
    public long id = 0L;

    @JsonProperty("username")
    public String name = "";

    public BeatmapOwner() {
    }

    @Override
    public String toString() {
        return "osuApi.BeatmapOwner(id=" + this.id + ",name=" + this.name + ")";
    }

    @Override
    public ObjectData get(String key) {
        return switch (key) {
            case "id" -> ObjectData.of(this.id);
            case "name" -> ObjectData.of(this.name);
            default -> {
                LOGGER.warn("Trying to get unknown key '{}' of BeatmapOwner!", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    @Override
    public String getType() {
        return "osuApi.Beatmap";
    }
}
