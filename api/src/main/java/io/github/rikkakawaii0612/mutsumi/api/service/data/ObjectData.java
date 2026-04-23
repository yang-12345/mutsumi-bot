package io.github.rikkakawaii0612.mutsumi.api.service.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>数据对象, 用于模块间的数据传输.
 *
 * <p>原则上, 我们不希望不同插件模块之间引用对方的类. 因此, 我们使用 {@link ObjectData}
 * 接口作为门面来传输数据结构.
 *
 * <p> // TODO
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
        LOGGER.warn("Unsupported casting: {} to Integer", this.getClass().getName());
        return 0;
    }

    default long asLong() {
        LOGGER.warn("Unsupported casting: {} to Long", this.getClass().getName());
        return 0L;
    }

    default double asDouble() {
        LOGGER.warn("Unsupported casting: {} to Double", this.getClass().getName());
        return 0.0D;
    }

    default boolean asBoolean() {
        LOGGER.warn("Unsupported casting: {} to Boolean", this.getClass().getName());
        return false;
    }

    // 请与 .toString() 区分
    default String asString() {
        LOGGER.warn("Unsupported casting: {} to String", this.getClass().getName());
        return "";
    }

    default ObjectData get(String key) {
        LOGGER.warn("Trying to get absent key '{}' of {}", key, this.getClass().getName());
        return EMPTY;
    }

    String getType();

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
