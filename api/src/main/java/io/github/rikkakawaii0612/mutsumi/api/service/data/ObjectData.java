package io.github.rikkakawaii0612.mutsumi.api.service.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>数据对象, 用于模块间的数据传输.
 *
 * <p>原则上, 我们不希望不同插件模块之间引用对方的类. 因此, 我们使用 {@link ObjectData}
 * 接口作为门面来传输数据结构.
 */
public interface ObjectData {
    Logger LOGGER = LoggerFactory.getLogger("BaseApi");
    
    ObjectData EMPTY = new ObjectData() {
        public int asInt() {
            LOGGER.warn("Unsupported casting: Empty to Integer");
            return 0;
        }

        public long asLong() {
            LOGGER.warn("Unsupported casting: Empty to Long");
            return 0L;
        }

        public double asDouble() {
            LOGGER.warn("Unsupported casting: Empty to Double");
            return 0.0D;
        }

        public boolean asBoolean() {
            LOGGER.warn("Unsupported casting: Empty to Boolean");
            return false;
        }

        public String asString() {
            LOGGER.warn("Unsupported casting: Empty to String");
            return "";
        }

        public ObjectData get(String key) {
            LOGGER.warn("Trying to get absent key '{}' of Empty", key);
            return EMPTY;
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

    default int asInt() {
        LOGGER.warn("Unsupported casting: {} to Integer", this.getType());
        return 0;
    }

    default long asLong() {
        LOGGER.warn("Unsupported casting: {} to Long", this.getType());
        return 0L;
    }

    default double asDouble() {
        LOGGER.warn("Unsupported casting: {} to Double", this.getType());
        return 0.0D;
    }

    default boolean asBoolean() {
        LOGGER.warn("Unsupported casting: {} to Boolean", this.getType());
        return false;
    }

    // 请与 .toString() 区分
    default String asString() {
        LOGGER.warn("Unsupported casting: {} to String", this.getType());
        return "";
    }

    default Object asObject() {
        return this.asObject(Object.class);
    }

    @SuppressWarnings("unchecked")
    default <T> T asObject(Class<T> type) {
        if (type.isAssignableFrom(String.class)) {
            return (T) this.asString();
        }
        LOGGER.warn("Unsupported casting: {} to {}", this.getType(), type.getName());
        return null;
    }

    default ObjectData get(String key) {
        LOGGER.warn("Trying to get absent key '{}' of {}", key, this.getType());
        return EMPTY;
    }

    String getType();

    default boolean is(String type) {
        return type.equals(this.getType());
    }

    default boolean isEmpty() {
        return false;
    }

    default int getInt(String key) {
        return this.get(key).asInt();
    }

    default long getLong(String key) {
        return this.get(key).asLong();
    }

    default double getDouble(String key) {
        return this.get(key).asDouble();
    }

    default boolean getBoolean(String key) {
        return this.get(key).asBoolean();
    }

    default String getString(String key) {
        return this.get(key).asString();
    }

    default Object getObject(String key) {
        return this.get(key).asObject();
    }

    default <T> T getObject(String key, Class<T> type) {
        return this.get(key).asObject(type);
    }

    static ObjectData of(int value) {
        return new IntegerObjectData(value);
    }

    static ObjectData of(long value) {
        return new LongObjectData(value);
    }

    static ObjectData of(double value) {
        return new DoubleObjectData(value);
    }

    static ObjectData of(boolean value) {
        return new BooleanObjectData(value);
    }

    static ObjectData of(String value) {
        return value != null ? new StringObjectData(value) : EMPTY;
    }

    static ListObjectData of(List<? extends ObjectData> value) {
        return value != null ? new ListObjectData(value) : ListObjectData.EMPTY;
    }

    static ListObjectData of(int[] value) {
        return value != null ?
                new ListObjectData(Arrays.stream(value).mapToObj(IntegerObjectData::new).toList())
                : ListObjectData.EMPTY;
    }

    static ListObjectData of(long[] value) {
        return value != null ?
                new ListObjectData(Arrays.stream(value).mapToObj(LongObjectData::new).toList())
                : ListObjectData.EMPTY;
    }

    static ListObjectData of(double[] value) {
        return value != null ?
                new ListObjectData(Arrays.stream(value).mapToObj(DoubleObjectData::new).toList())
                : ListObjectData.EMPTY;
    }

    static ListObjectData of(boolean[] value) {
        if (value == null) {
            return ListObjectData.EMPTY;
        }
        List<BooleanObjectData> list = new ArrayList<>();
        for (boolean bl : value) {
            list.add(new BooleanObjectData(bl));
        }
        return new ListObjectData(list);
    }

    static ListObjectData of(String[] value) {
        return value != null ?
                new ListObjectData(Arrays.stream(value).map(StringObjectData::new).toList())
                : ListObjectData.EMPTY;
    }

    static ListObjectData of(ObjectData[] value) {
        return value != null ? new ListObjectData(List.of(value)) : ListObjectData.EMPTY;
    }

    static ObjectData of(ObjectData value) {
        return value != null ? value : EMPTY;
    }
}
