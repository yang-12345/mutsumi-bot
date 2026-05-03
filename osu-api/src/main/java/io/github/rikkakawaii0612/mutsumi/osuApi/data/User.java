package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @JsonProperty("is_online")
    public boolean online = false;

    @JsonProperty("statistics")
    public UserStatistics statistics;

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
