package io.github.rikkakawaii0612.mutsumi.api.service;

import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

public interface ServiceReference {
    Service get();

    default ObjectData call(ServiceRequest request) {
        return this.get().call(request);
    }
}
