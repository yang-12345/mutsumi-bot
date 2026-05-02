package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import java.util.function.Consumer;

public record Reference<T>(T value) {
    public Reference<T> let(Consumer<T> action) {
        action.accept(this.value);
        return this;
    }
}
