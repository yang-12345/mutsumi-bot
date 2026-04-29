package io.github.rikkakawaii0612.mutsumi.api;

public interface Service {
    void load(String id, ServiceLookup lookup);

    void unload();
}
