package io.github.rikkakawaii0612.mutsumi.api.contact.message;

public interface Text extends SingleMessage {
    String getText();

    @Override
    default String asString() {
        return this.getText();
    }
}
