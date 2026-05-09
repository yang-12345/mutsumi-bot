package io.github.rikkakawaii0612.mutsumi.api.contact.message;

public record Text(String content) implements SingleMessage {
    @Override
    public String asString() {
        return this.content();
    }
}
