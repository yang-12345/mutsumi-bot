package io.github.rikkakawaii0612.mutsumi.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceLocator;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceNotFoundException;
import org.pf4j.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceModule extends Plugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("Service API");
    protected final ModuleContext context;

    public ServiceModule(ModuleContext context) {
        this.context = context;
    }

    @Override
    public final void start() {
        try {
            this.load();
        } catch (ServiceNotFoundException e) {
            LOGGER.error("Service Module '{}' cannot find service while loading: ", this.context.id(), e);
        }
    }

    protected abstract void load();

    @Override
    public final void stop() {
        try {
            this.unload();
        } catch (ServiceNotFoundException e) {
            LOGGER.error("Service Module '{}' cannot find service while unloading: ", this.context.id(), e);
        }
    }

    protected abstract void unload();

    public JsonNode getConfig() {
        return this.context.getConfig(this.context.id());
    }

    public MutsumiBot getBot() {
        return this.context.getBot();
    }

    public ServiceLocator getServiceLocator() {
        return this.context.getServiceLocator();
    }
}
