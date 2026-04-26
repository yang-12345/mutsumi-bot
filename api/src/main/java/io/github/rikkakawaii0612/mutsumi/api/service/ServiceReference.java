package io.github.rikkakawaii0612.mutsumi.api.service;

import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

import java.util.Collection;
import java.util.List;

public interface ServiceReference {
    Service get();

    default ObjectData call(ServiceRequest request) {
        return this.get().call(request);
    }

    default List<ObjectData> call(Collection<ServiceRequest> requests) {
        return this.get().call(requests);
    }
}
