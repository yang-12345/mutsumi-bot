package io.github.rikkakawaii0612.mutsumi.api.service;

import java.util.Optional;

public interface ServiceLocator {
    Optional<Service> getService(String id);
}
