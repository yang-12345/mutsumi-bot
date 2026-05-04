package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap.beatmap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Generators {
    private static final Map<String, GeneratorFactory> GENERATORS = new HashMap<>();

    public static void register(String type, GeneratorFactory factory) {
        GENERATORS.put(type.toLowerCase(Locale.ROOT), factory);
    }

    public static Generator createGenerator(String type) {
        String str = type.toLowerCase(Locale.ROOT);
        if (!GENERATORS.containsKey(str)) {
            return null;
        }
        return GENERATORS.get(str).create();
    }

    public interface GeneratorFactory {
        Generator create();
    }

    static {
        register("jack", JackGenerator::new);
        register("speed", SpeedGenerator::new);
    }
}
