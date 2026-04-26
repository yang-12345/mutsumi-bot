package io.github.rikkakawaii0612.mutsumi.api.service.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListObjectData implements ObjectData {
    public static final ListObjectData EMPTY = new ListObjectData(List.of());
    private final List<? extends ObjectData> values;

    public ListObjectData(List<? extends ObjectData> values) {
        this.values = List.copyOf(values);
    }

    public List<? extends ObjectData> getValues() {
        return this.values;
    }

    @Override
    public String toString() {
        return "List" + this.values.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T asObject(Class<T> type) {
        if (type.isAssignableFrom(Iterable.class)) {
            return (T) this.values;
        }
        return ObjectData.super.asObject(type);
    }

    @Override
    public ObjectData get(String key) {
        try {
            return this.values.get(Integer.parseInt(key));
        } catch (NumberFormatException _) {
            return ObjectData.EMPTY;
        }
    }

    @Override
    public String getType() {
        return "base.List";
    }

    public static List<? extends ObjectData> read(ObjectData data) {
        if (data instanceof ListObjectData listObjectData) {
            return List.copyOf(listObjectData.values);
        } else {
            return List.of();
        }
    }

    public static List<? extends ObjectData> readAsMutable(ObjectData data) {
        return new ArrayList<>(read(data));
    }
}
