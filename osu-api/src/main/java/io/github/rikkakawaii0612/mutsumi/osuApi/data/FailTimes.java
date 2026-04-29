package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FailTimes {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

    @JsonProperty("exit")
    public int[] exit = new int[100];

    @JsonProperty("fail")
    public int[] fail = new int[100];

    public FailTimes() {
    }

    @Override
    public String toString() {
        return "osuApi.FailTimes";
    }
}
