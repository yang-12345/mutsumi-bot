package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.*;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import io.github.rikkakawaii0612.mutsumi.api.util.DuplicatableObjectIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beatmapset implements ObjectData {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

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

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            resolver = DuplicatableObjectIdResolver.class)
    public List<Beatmap> beatmaps;

    @Override
    public String toString() {
        return "osuApi.Beatmapset(id=" + this.id + ")";
    }

    @Override
    public ObjectData get(String key) {
        return switch (key) {
            case "id" -> ObjectData.of(this.id);
            case "artist" -> ObjectData.of(this.artist);
            case "artistUnicode" -> ObjectData.of(this.artistUnicode);
            case "title" -> ObjectData.of(this.title);
            case "titleUnicode" -> ObjectData.of(this.titleUnicode);
            case "creator" -> ObjectData.of(this.creator);
            case "beatmaps" -> ObjectData.of(this.beatmaps);
            default -> {
                LOGGER.warn("Trying to get unknown key '{}' of Beatmapset!", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    @Override
    public String getType() {
        return "osuApi.Beatmapset";
    }
}
