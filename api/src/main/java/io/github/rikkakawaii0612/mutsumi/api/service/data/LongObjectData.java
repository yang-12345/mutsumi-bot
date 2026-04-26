package io.github.rikkakawaii0612.mutsumi.api.service.data;

public class LongObjectData implements ObjectData {
    private final long value;

    public LongObjectData(long value) {
        this.value = value;
    }

    @Override
    public int asInt() {
        return Math.toIntExact(this.value);
    }

    @Override
    public long asLong() {
        return this.value;
    }

    @Override
    public double asDouble() {
        return this.value;
    }

    @Override
    public boolean asBoolean() {
        return this.value != 0;
    }

    @Override
    public String asString() {
        return String.valueOf(this.value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T asObject(Class<T> type) {
        if (type.isAssignableFrom(Long.class)) {
            return (T) Long.valueOf(this.value);
        }
        return ObjectData.super.asObject(type);
    }

    @Override
    public ObjectData get(String key) {
        return EMPTY;
    }

    @Override
    public String getType() {
        return "base.Long";
    }
}
