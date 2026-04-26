package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceLocator;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceNotFoundException;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceReference;
import io.github.rikkakawaii0612.mutsumi.service.ModuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceLocatorImpl implements ServiceLocator {
    private final ModuleManager moduleManager;
    private final List<Service> caches = new ArrayList<>();

    public ServiceLocatorImpl(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public ServiceReference getService(String id) {
        return () -> {
            for (Service service : this.caches) {
                if (id.equals(service.getId())) {
                    return service;
                }
            }
            throw new ServiceNotFoundException();
        };
    }

    public void update() {
        this.caches.clear();
        this.caches.addAll(this.moduleManager.getExtensions(Service.class));
    }
}
