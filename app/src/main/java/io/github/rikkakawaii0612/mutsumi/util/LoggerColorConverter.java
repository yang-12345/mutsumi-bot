package io.github.rikkakawaii0612.mutsumi.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

public class LoggerColorConverter extends CompositeConverter<ILoggingEvent> {
    @Override
    public String transform(ILoggingEvent event, String in) {
        // 根据级别返回带 ANSI 颜色的字符串
        return switch (event.getLevel().toInt()) {
            case Level.ERROR_INT -> "\033[31m" + in + "\033[0m";
            case Level.WARN_INT -> "\033[93m" + in + "\033[0m";
            case Level.INFO_INT -> "\033[39m" + in + "\033[0m";
            case Level.DEBUG_INT -> "\033[96m" + in + "\033[0m";
            default -> in;
        };
    }
}
