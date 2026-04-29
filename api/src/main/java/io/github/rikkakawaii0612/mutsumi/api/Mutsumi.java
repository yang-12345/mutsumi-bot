package io.github.rikkakawaii0612.mutsumi.api;

import io.github.rikkakawaii0612.mutsumi.api.contact.handler.MessageHandler;

public interface Mutsumi {
    //TODO: 模块重载时移除; 移至 BotManager
    void addMessageListener(MessageHandler handler);
}
