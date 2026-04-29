package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

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

    public User() {
    }

    @Override
    public String toString() {
        return "osuApi.User(id=" + this.id + ",name=" + this.username + ")";
    }
}
