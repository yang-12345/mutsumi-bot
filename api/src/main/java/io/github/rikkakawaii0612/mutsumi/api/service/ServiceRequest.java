package io.github.rikkakawaii0612.mutsumi.api.service;

import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;

import java.util.HashMap;
import java.util.Map;

public class ServiceRequest {
    private final Map<String, String> headers;
    private final Map<String, ObjectData> data;

    private ServiceRequest(Map<String, String> headers, Map<String, ObjectData> data) {
        this.headers = headers;
        this.data = data;
    }

    public String getHeader(String key) {
        return this.headers.getOrDefault(key, "");
    }

    public ObjectData getData(String key) {
        return this.data.getOrDefault(key, ObjectData.EMPTY);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        Map<String, String> headers = new HashMap<>();
        Map<String, ObjectData> data = new HashMap<>();

        private Builder() {
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder data(String key, ObjectData value) {
            this.data.put(key, value);
            return this;
        }

        public ServiceRequest build() {
            return new ServiceRequest(Map.copyOf(this.headers), Map.copyOf(this.data));
        }
    }
}
