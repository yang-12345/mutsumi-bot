package io.github.rikkakawaii0612.mutsumi.api.contact.message;

public record ByteArrayImage(byte[] data) implements Image {
    @Override
    public byte[] getData() {
        return this.data;
    }
}
