package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.*;
import io.github.rikkakawaii0612.mutsumi.api.util.DuplicatableObjectIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beatmapset {@JsonProperty("id")
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

    @JsonProperty("covers")
    public Covers covers;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            resolver = DuplicatableObjectIdResolver.class)
    public List<Beatmap> beatmaps;

    @Override
    public String toString() {
        return "osuApi.Beatmapset(id=" + this.id + ")";
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Covers {
        @JsonProperty("cover")
        public String cover = "";

        @JsonProperty("cover@2x")
        public String cover2x = "";

        @JsonProperty("card")
        public String card = "";

        @JsonProperty("card@2x")
        public String card2x = "";

        @JsonProperty("list")
        public String list = "";

        @JsonProperty("list@2x")
        public String list2x = "";

        @JsonProperty("slimcover")
        public String slimCover = "";

        @JsonProperty("slimcover@2x")
        public String slimCover2x = "";

        public Covers() {
        }

        @Override
        public String toString() {
            return "osuApi.Covers";
        }
    }

}
