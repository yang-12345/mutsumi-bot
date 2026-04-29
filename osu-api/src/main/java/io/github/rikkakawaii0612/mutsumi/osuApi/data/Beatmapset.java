package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.*;
import io.github.rikkakawaii0612.mutsumi.api.util.DuplicatableObjectIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beatmapset {
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
}
