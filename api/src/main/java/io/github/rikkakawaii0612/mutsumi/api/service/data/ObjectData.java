package io.github.rikkakawaii0612.mutsumi.api.service.data;

import java.util.List;

public interface ObjectData {
    ObjectData EMPTY = new ObjectData() {
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
            return "";
        }

        @Override
        public String getType() {
            return "empty";
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    };

    int asInt();

    long asLong();

    double asDouble();

    boolean asBoolean();

    String asString();

    String getType();

    default boolean isEmpty() {
        return false;
    }

    static IntegerObjectData of(int value) {
        return new IntegerObjectData(value);
    }

    static LongObjectData of(long value) {
        return new LongObjectData(value);
    }

    static DoubleObjectData of(double value) {
        return new DoubleObjectData(value);
    }

    static BooleanObjectData of(boolean value) {
        return new BooleanObjectData(value);
    }

    static StringObjectData of(String value) {
        return new StringObjectData(value);
    }

    static ListObjectData of(List<? extends ObjectData> value) {
        return new ListObjectData(value != null ? value : List.of());
    }

    static ObjectData of(ObjectData value) {
        return value != null ? value : EMPTY;
    }
}
