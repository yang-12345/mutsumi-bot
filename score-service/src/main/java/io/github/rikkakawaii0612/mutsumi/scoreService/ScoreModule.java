package io.github.rikkakawaii0612.mutsumi.scoreService;

import io.github.rikkakawaii0612.mutsumi.api.ModuleContext;
import io.github.rikkakawaii0612.mutsumi.api.ServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreModule extends ServiceModule {
    private static final Logger LOGGER = LoggerFactory.getLogger("ScoreService");

    public ScoreModule(ModuleContext context) {
        super(context);
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }
}
