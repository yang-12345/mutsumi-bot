package io.github.rikkakawaii0612.mutsumi.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceLocator;

public interface ModuleContext {
    JsonNode getConfig(String id);

    String id();

    ServiceLocator getServiceLocator();
}
