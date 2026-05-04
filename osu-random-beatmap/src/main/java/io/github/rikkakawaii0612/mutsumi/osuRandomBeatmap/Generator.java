package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

public interface Generator {
    int getCurrentTime();

    HitObject[] next();
}
