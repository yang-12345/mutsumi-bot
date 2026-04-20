package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.databind.JsonNode;

public class DataUtils {
    public static int getInt(JsonNode node, String field) {
        return getInt(node, field, 0);
    }

    public static int getInt(JsonNode node, String field, int defaultVar) {
        if (!node.has(field)) {
            return defaultVar;
        }
        JsonNode jsonNode = node.get(field);
        return jsonNode.asInt(defaultVar);
    }

    public static long getLong(JsonNode node, String field) {
        return getLong(node, field, 0L);
    }

    public static long getLong(JsonNode node, String field, long defaultVar) {
        if (!node.has(field)) {
            return defaultVar;
        }
        JsonNode jsonNode = node.get(field);
        return jsonNode.asLong(defaultVar);
    }

    public static double getDouble(JsonNode node, String field) {
        return getDouble(node, field, 0.0D);
    }

    public static double getDouble(JsonNode node, String field, double defaultVar) {
        if (!node.has(field)) {
            return defaultVar;
        }
        JsonNode jsonNode = node.get(field);
        return jsonNode.asDouble(defaultVar);
    }

    public static String getString(JsonNode node, String field) {
        return getString(node, field, "");
    }

    public static String getString(JsonNode node, String field, String defaultVar) {
        if (!node.has(field)) {
            return defaultVar;
        }
        JsonNode jsonNode = node.get(field);
        return jsonNode.asText(defaultVar);
    }
}
