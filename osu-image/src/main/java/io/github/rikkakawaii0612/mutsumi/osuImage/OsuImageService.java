package io.github.rikkakawaii0612.mutsumi.osuImage;

import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class OsuImageService implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuImageService");

    @Override
    public void load(String id, ServiceLookup lookup) {
        loadFont("assets/fonts/Torus-Light.otf");
        loadFont("assets/fonts/Torus-Regular.otf");
        loadFont("assets/fonts/Torus-SemiBold.otf");
    }

    @Override
    public void unload() {
    }

    private static void loadFont(String path) {
        try (InputStream fontStream = OsuImageService.class.getClassLoader().getResourceAsStream(path)) {
            if (fontStream == null) {
                LOGGER.warn("Font file not found at: {}", path);
                return;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            LOGGER.warn("Failed to load font file at '{}': ", path, e);
        }
    }
}
