package io.github.rikkakawaii0612.mutsumi.api;

import com.fasterxml.jackson.databind.JsonNode;

public interface Config {
    JsonNode getOrCreate(String id);
}
