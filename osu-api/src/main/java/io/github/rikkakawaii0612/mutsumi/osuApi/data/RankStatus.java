package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.LowerCaseStrategy.class)
public enum RankStatus {
    GRAVEYARD("graveyard"),
    WIP("wip"),
    PENDING("pending"),
    RANKED("ranked"),
    APPROVED("approved"),
    QUALIFIED("qualified"),
    LOVED("loved");

    @JsonValue
    private final String name;

    RankStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
