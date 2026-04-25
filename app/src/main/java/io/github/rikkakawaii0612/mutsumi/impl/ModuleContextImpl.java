package io.github.rikkakawaii0612.mutsumi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rikkakawaii0612.mutsumi.api.ModuleContext;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ModuleContextImpl implements ModuleContext {
    private static final Logger LOGGER = LoggerFactory.getLogger("ModuleManager");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String id;
    private final ServiceLocator serviceLocator;
    private final Map<String, JsonNode> configs = new ConcurrentHashMap<>();

    public ModuleContextImpl(String id, ServiceLocator serviceLocator) {
        this.id = id;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public JsonNode getConfig(String id) {
        return this.configs.computeIfAbsent(id, str -> {
            try (InputStream is = new FileInputStream(Paths.get("config", str + ".json").toFile())) {
                return OBJECT_MAPPER.readTree(is);
            } catch (FileNotFoundException _) {
                LOGGER.warn("Config '{}.json' does not exist", str);
                return OBJECT_MAPPER.createObjectNode();
            } catch (Exception e) {
                LOGGER.warn("Cannot read config '{}.json': ", str, e);
                return OBJECT_MAPPER.createObjectNode();
            }
        });
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public ServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }
}
