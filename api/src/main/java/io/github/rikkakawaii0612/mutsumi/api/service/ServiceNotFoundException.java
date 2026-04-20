package io.github.rikkakawaii0612.mutsumi.api.service;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException() {
        super();
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }
}
