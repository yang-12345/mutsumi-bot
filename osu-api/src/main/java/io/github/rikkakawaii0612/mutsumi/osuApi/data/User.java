package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements ObjectData {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

    @JsonProperty("id")
    public long id = 0L;

    @JsonProperty("username")
    public String name = "";

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
        return "osuApi.User(id=" + this.id + ",name=" + this.name + ")";
    }

    @Override
    public ObjectData get(String key) {
        return switch (key) {
            case "id" -> ObjectData.of(this.id);
            case "name" -> ObjectData.of(this.name);
            case "playMode" -> ObjectData.of(this.playMode.getName());
            case "avatarUrl" -> ObjectData.of(this.avatarUrl);
            case "online" -> ObjectData.of(this.online);
            default -> {
                LOGGER.warn("Trying to get unknown key '{}' of User!", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    @Override
    public String getType() {
        return "osuApi.User";
    }
}
