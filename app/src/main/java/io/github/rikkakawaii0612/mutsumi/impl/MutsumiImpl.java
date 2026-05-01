package io.github.rikkakawaii0612.mutsumi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rikkakawaii0612.mutsumi.api.Config;
import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.loader.MutsumiServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class MutsumiImpl implements Mutsumi {
    private static final Logger LOGGER = LoggerFactory.getLogger("Mutsumi");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Bot 总线 (不是公交车)
    private final BotBusImpl botBus;

    // Mutsumi 服务加载器, 通常不对外部服务开放
    private final MutsumiServiceLoader serviceLoader;

    // 配置存储
    private final Map<String, JsonNode> configs = new HashMap<>();
    private final Config configImpl = id -> this.configs.computeIfAbsent(id, this::createConfig);

    public MutsumiImpl() {
        this.botBus = new BotBusImpl(this);
        this.serviceLoader = new MutsumiServiceLoader(this);
    }

    public void runBots() {
        this.botBus.start();
    }

    @Override
    public BotBusImpl getBotBus() {
        return this.botBus;
    }

    // 应某些人的要求, 你要的 Arisu 称呼这不就写出来了嘛 (
    @Override
    public String getName() {
        JsonNode jsonNode = this.configImpl.getOrCreate("mutsumi");
        return jsonNode.has("name") ? jsonNode.get("name").asText() : "Mutsumi";
    }

    @Override
    public Config getConfig() {
        return this.configImpl;
    }

    /*
     * 这些是内部方法, 其它服务不应调用
     */

    public MutsumiServiceLoader getServiceLoader() {
        return this.serviceLoader;
    }

    public void loadConfigs() {
        this.configs.clear();
        try {
            File configsDir = new File("configs");
            if (!configsDir.isDirectory()) {
                Files.createDirectory(configsDir.toPath());
                return;
            }
            File[] files = configsDir.listFiles((_, name) -> name.toLowerCase().endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    try (InputStream is = new FileInputStream(file)) {
                        this.configs.put(name.substring(0, name.length() - 5), OBJECT_MAPPER.readTree(is));
                    } catch (Exception e) {
                        LOGGER.warn("Cannot read config '{}': ", name, e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot read configs: ", e);
        }

        LOGGER.info("Loaded {} config file(s)", this.configs.size());
    }

    private JsonNode createConfig(String id) {
        JsonNode node = OBJECT_MAPPER.createObjectNode();
        try {
            OBJECT_MAPPER.writeValue(Paths.get("configs", id + ".json").toFile(), node);
        } catch (IOException e) {
            LOGGER.error("Failed to create config for service '{}': ", id, e);
        }
        return node;
    }
}
