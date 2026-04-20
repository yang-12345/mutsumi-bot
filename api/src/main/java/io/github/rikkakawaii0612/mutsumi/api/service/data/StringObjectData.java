package io.github.rikkakawaii0612.mutsumi.api.service.data;

public class StringObjectData implements ObjectData {
    private final String value;

    public StringObjectData(String value) {
        this.value = value;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0L;
    }

    @Override
    public double asDouble() {
        return 0.0D;
    }

    @Override
    public boolean asBoolean() {
        return !this.value.isEmpty();
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public String getType() {
        return "base.String";
    }
}
