package io.github.rikkakawaii0612.mutsumi.osuApi;

import io.github.rikkakawaii0612.mutsumi.api.ModuleContext;
import io.github.rikkakawaii0612.mutsumi.api.ServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsuApiModule extends ServiceModule {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

    public OsuApiModule(ModuleContext context) {
        super(context);
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
        LOGGER.info("Stopped OsuApiModule");
    }
}
