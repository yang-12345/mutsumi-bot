package io.github.rikkakawaii0612.mutsumi.api.service.data;

public class BooleanObjectData implements ObjectData {
    private final boolean value;

    public BooleanObjectData(boolean value) {
        this.value = value;
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
    @SuppressWarnings("unchecked")
    public <T> T asObject(Class<T> type) {
        if (type.isAssignableFrom(Boolean.class)) {
            return (T) Boolean.valueOf(this.value);
        }
        return ObjectData.super.asObject(type);
    }

    @Override
    public ObjectData get(String key) {
        return EMPTY;
    }

    @Override
    public String getType() {
        return "base.Boolean";
    }
}
