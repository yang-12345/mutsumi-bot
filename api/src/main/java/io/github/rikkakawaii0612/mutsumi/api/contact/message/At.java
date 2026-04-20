package io.github.rikkakawaii0612.mutsumi.api.contact.message;

public interface At extends SingleMessage {
    long getTarget();

    @Override
    default String asString() {
        return "@" + this.getTarget();
    }
}
