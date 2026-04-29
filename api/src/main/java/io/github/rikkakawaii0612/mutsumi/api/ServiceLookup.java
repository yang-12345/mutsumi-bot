package io.github.rikkakawaii0612.mutsumi.api;

import java.util.List;
import java.util.Map;

public final class ServiceLookup {
    private final Mutsumi mutsumi;
    private final Map<String, Wrapper> services;

    public ServiceLookup(Mutsumi mutsumi, Map<String, Wrapper> services) {
        this.mutsumi = mutsumi;
        this.services = services;
    }

    public Mutsumi getMutsumi() {
        return this.mutsumi;
    }

    public Wrapper getService(String id) {
        return this.services.get(id);
    }

    public record Wrapper(Service service,
                          String id,
                          String version,
                          String author,
                          List<String> dependencies,
                          Config config) {
    }
}
