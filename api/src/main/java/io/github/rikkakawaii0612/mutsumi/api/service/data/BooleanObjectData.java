package io.github.rikkakawaii0612.mutsumi.api.service.data;

public class BooleanObjectData implements ObjectData {
    private final boolean value;

    public BooleanObjectData(boolean value) {
        this.value = value;
    }

    @Override
    public int asInt() {
        return this.value ? 1 : 0;
    }

    @Override
    public long asLong() {
        return this.value ? 1L : 0L;
    }

    @Override
    public double asDouble() {
        return this.value ? 1.0D : 0.0D;
    }

    @Override
    public boolean asBoolean() {
        return this.value;
    }

    @Override
    public String asString() {
        return String.valueOf(this.value);
    }

    @Override
    public String getType() {
        return "base.Boolean";
    }
}
