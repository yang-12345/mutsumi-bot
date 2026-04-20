package io.github.rikkakawaii0612.mutsumi.guessService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class AliasSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger("GuessService");
    private final Map<String, Set<String>> aliases = new HashMap<>();

    public Set<String> getAliases(String title) {
        if (this.aliases.containsKey(title)) {
            return this.aliases.get(title);
        }
        return Set.of();
    }

    public void loadConfig(JsonNode root) {
        Map<String, Set<String>> map = new HashMap<>();
        int songsWithAliases = 0, totalAliases = 0;
        try {
            for (Iterator<Map.Entry<String, JsonNode>> it = root.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String key = entry.getKey();
                JsonNode node = entry.getValue();
                if (!node.isArray()) {
                    throw new IllegalArgumentException("Failed to load alias config: '" + key + "' is not an array");
                }
                if (node.isEmpty()) {
                    continue;
                }
                songsWithAliases++;
                HashSet<String> set = new HashSet<>();
                for (JsonNode jsonNode : node) {
                    if (set.add(jsonNode.asText())) {
                        totalAliases++;
                    }
                }
                map.put(key, set);
            }
            this.aliases.clear();
            this.aliases.putAll(map);
            LOGGER.info("Loaded {} aliases of {} songs", totalAliases, songsWithAliases);
        } catch (Exception e) {
            LOGGER.error("Failed to load alias config: ", e);
        }
    }
}
