package io.github.rikkakawaii0612.mutsumi.api.service.data;

public class StringObjectData implements ObjectData {
    private final String value;

    public StringObjectData(String value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public ObjectData get(String key) {
        return EMPTY;
    }

    @Override
    public String getType() {
        return "base.String";
    }
}
