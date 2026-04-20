package io.github.rikkakawaii0612.mutsumi.util;

import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This logger will only log warns and errors
public class BotLogger implements MiraiLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("Mutsumi");

    public BotLogger() {
    }

    @Override
    public void verbose(@Nullable String s, @Nullable Throwable throwable) {
    }

    @Override
    public void debug(@Nullable String s) {
    }

    @Override
    public void info(@Nullable String s, @Nullable Throwable throwable) {
    }

    @Override
    public void warning(@Nullable String s, @Nullable Throwable throwable) {
        LOGGER.warn(s, throwable);
    }

    @Nullable
    @Override
    public String getIdentity() {
        return "mutsumi-bot-mirai";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void debug(@Nullable String s, @Nullable Throwable throwable) {
    }

    @Override
    public void error(@Nullable String s, @Nullable Throwable throwable) {
        LOGGER.error(s, throwable);
    }

    @Override
    public boolean isVerboseEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;

    }
    @Override
    public boolean isWarningEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void verbose(@Nullable String s) {
    }

    @Override
    public void info(@Nullable String s) {
    }

    @Override
    public void warning(@Nullable String s) {
        if (s != null) {
            LOGGER.warn(s);
        }
    }

    @Override
    public void error(@Nullable String s) {
        if (s != null) {
            LOGGER.error(s);
        }
    }
}