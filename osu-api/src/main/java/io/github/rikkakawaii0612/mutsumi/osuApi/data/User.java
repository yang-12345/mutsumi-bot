package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @JsonProperty("id")
    public long id = 0L;

    @JsonProperty("username")
    public String username = "";

    @JsonProperty("playmode")
    public PlayMode playMode = PlayMode.STANDARD;

    @JsonProperty("avatar_url")
    public String avatarUrl = "";

    @JsonProperty("country_code")
    public String countryCode = "";

    @JsonProperty("default_group")
    public String defaultGroup;

    @JsonProperty("is_active")
    public boolean active = false;

    @JsonProperty("is_bot")
    public boolean bot = false;

    @JsonProperty("is_deleted")
    public boolean deleted = false;

    @JsonProperty("is_online")
    public boolean online = false;

    @JsonProperty("is_supporter")
    public boolean supporter = false;

    @JsonProperty("last_visit")
    public String lastVisit;

    @JsonProperty("pm_friends_only")
    public boolean pmFriendsOnly = false;

    @JsonProperty("profile_colour")
    public String profileColour;

    @JsonProperty("statistics")
    public UserStatistics statistics;

    @JsonProperty("beatmap_playcounts_count")
    public int beatmapPlaycountsCount;

    public User() {
    }

    @Override
    public String toString() {
        return "osuApi.User(id=" + this.id + ",name=" + this.username + ")";
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserStatistics {
        @JsonProperty("pp")
        public double pp = 0.0D;

        public UserStatistics() {
        }

        @Override
        public String toString() {
            return "osuApi.UserStatistics";
        }
    }

}
