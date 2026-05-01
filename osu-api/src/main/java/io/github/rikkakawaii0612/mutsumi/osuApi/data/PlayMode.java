package io.github.rikkakawaii0612.mutsumi.osuApi.data;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * osu! 的四个模式.
 */
public enum PlayMode {
    STANDARD("osu"),
    TAIKO("taiko"),
    CATCH("fruits"),
    MANIA("mania");

    @JsonValue
    private final String name;

    PlayMode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * 匹配字符串并返回其对应的游戏模式 (忽略前后空白, 大小写).
     *
     * @param str 要匹配的字符串
     * @return 字符串对应的游戏模式. 如果没有对应模式, 则返回 null.
     */
    public static PlayMode of(String str) {
        return switch (str.toLowerCase(Locale.ROOT)) {
            case "standard", "std", "0" -> STANDARD;
            case "taiko", "1" -> TAIKO;
            case "catch", "fruit", "fruits", "ctb", "2" -> CATCH;
            case "mania", "3" -> MANIA;
            default -> null;
        };
    }
}
