package io.github.rikkakawaii0612.mutsumi.api.service.data;

import java.util.List;

public class ListObjectData implements ObjectData {
    private final List<? extends ObjectData> values;

    public ListObjectData(List<? extends ObjectData> values) {
        this.values = List.copyOf(values);
    }

    public List<? extends ObjectData> getValues() {
        return this.values;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public String asString() {
        return "List[]";
    }

    @Override
    public String getType() {
        return "base.List";
    }

    public static List<? extends ObjectData> read(ObjectData data) {
        if (data instanceof ListObjectData listObjectData) {
            return listObjectData.values;
        } else {
            return List.of();
        }
    }
}
