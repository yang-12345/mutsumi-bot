package io.github.rikkakawaii0612.mutsumi.guessService;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rikkakawaii0612.mutsumi.api.ModuleContext;
import io.github.rikkakawaii0612.mutsumi.api.ServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuessModule extends ServiceModule {
    private static final Logger LOGGER = LoggerFactory.getLogger("GuessService");
    private static GuessModule INSTANCE;

    public final AliasSystem aliasSystem = new AliasSystem();

    public GuessModule(ModuleContext context) {
        super(context);
    }

    @Override
    public void load() {
        INSTANCE = this;
        JsonNode config = this.context.getConfig("guess-service");
        this.aliasSystem.loadConfig(config);
    }

    @Override
    public void unload() {
        INSTANCE = null;
    }

    public static GuessModule getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Module is not available");
        }
        return INSTANCE;
    }
}
