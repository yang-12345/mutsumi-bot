package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements ObjectData {
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
    public int asInt() {
        return Math.toIntExact(this.id);
    }

    @Override
    public long asLong() {
        return this.id;
    }

    @Override
    public double asDouble() {
        return this.id;
    }

    @Override
    public boolean asBoolean() {
        return this.id != 0L;
    }

    @Override
    public String asString() {
        return "osuApi.User(id=" + this.id + ",name=" + this.name + ")";
    }

    @Override
    public String getType() {
        return "osuApi.User";
    }
}
