package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.*;
import io.github.rikkakawaii0612.mutsumi.api.util.DuplicatableObjectIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beatmap {
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

    @JsonProperty("total_length")
    public int totalLength = 0;

    @JsonProperty("user_id")
    public long userId = 0L;

    @JsonProperty("beatmapset")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            resolver = DuplicatableObjectIdResolver.class)
    public Beatmapset beatmapset;

    @JsonProperty("checksum")
    public String checksum;

    @JsonProperty("current_user_playcount")
    public int currentUserPlaycount = 0;

    @JsonProperty("max_combo")
    public int maxCombo = 0;

    @JsonProperty("failtimes")
    public FailTimes failTimes;

    /*
     * Attributes of BeatmapExtended
     */

    @JsonProperty("accuracy")
    public double od = 0.0D;

    @JsonProperty("ar")
    public double ar = 0.0D;

    @JsonProperty("cs")
    public double cs = 0.0D;

    @JsonProperty("drain")
    public double drain = 0.0D;

    @JsonProperty("bpm")
    public double bpm = 0.0D;

    @JsonProperty("hit_length")
    public int hitLength = 0;

    @JsonProperty("is_scoreable")
    public boolean scoreable = false;

    @JsonProperty("convert")
    public boolean convert = false;

    @JsonProperty("count_circles")
    public int countCircles = 0;

    @JsonProperty("count_sliders")
    public int countSliders = 0;

    @JsonProperty("count_spinners")
    public int countSpinners = 0;

    @JsonProperty("passcount")
    public int passCount;

    @JsonProperty("owners")
    public BeatmapOwner[] owners;

    @JsonProperty("deleted_at")
    public String deletedAt;

    @JsonProperty("last_updated")
    public String lastUpdated;

    @JsonProperty("url")
    public String url;

    public Beatmap() {
    }

    @Override
    public String toString() {
        return "osuApi.Beatmap(id=" + this.id + ")";
    }
}
