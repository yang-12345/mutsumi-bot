package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceLocator;
import io.github.rikkakawaii0612.mutsumi.service.ModuleManager;

import java.util.List;
import java.util.Optional;

public class ServiceLocatorImpl implements ServiceLocator {
    private final ModuleManager moduleManager;

    public ServiceLocatorImpl(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public Optional<Service> getService(String id) {
        List<Service> list = this.moduleManager.getExtensions(Service.class);
        for (Service service : list) {
            if (id.equals(service.getId())) {
                return Optional.of(service);
            }
        }

        return Optional.empty();
    }
}
