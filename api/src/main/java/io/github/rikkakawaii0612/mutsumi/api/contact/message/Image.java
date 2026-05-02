package io.github.rikkakawaii0612.mutsumi.api.contact.message;

import java.util.Arrays;

public interface Image extends SingleMessage {
    byte[] getData();

    @Override
    default String asString() {
        return "Image@" + Arrays.hashCode(this.getData());
    }
}
