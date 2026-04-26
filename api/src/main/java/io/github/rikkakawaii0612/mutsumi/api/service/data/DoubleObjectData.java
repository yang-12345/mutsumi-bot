package io.github.rikkakawaii0612.mutsumi.api.service.data;

public class DoubleObjectData implements ObjectData {
    private final double value;

    public DoubleObjectData(double value) {
        this.value = value;
    }

    @Override
    public int asInt() {
        return (int) this.value;
    }

    @Override
    public long asLong() {
        return (long) this.value;
    }

    @Override
    public double asDouble() {
        return this.value;
    }

    @Override
    public boolean asBoolean() {
        return this.value != 0.0D;
    }

    @Override
    public String asString() {
        return String.valueOf(this.value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T asObject(Class<T> type) {
        if (type.isAssignableFrom(Double.class)) {
            return (T) Double.valueOf(this.value);
        }
        return ObjectData.super.asObject(type);
    }

    @Override
    public ObjectData get(String key) {
        return EMPTY;
    }

    @Override
    public String getType() {
        return "base.Double";
    }
}
