package io.github.rikkakawaii0612.mutsumi.api.contact.message;

public record At(long target) implements SingleMessage {
    @Override
    public String asString() {
        return "@" + this.target();
    }
}
