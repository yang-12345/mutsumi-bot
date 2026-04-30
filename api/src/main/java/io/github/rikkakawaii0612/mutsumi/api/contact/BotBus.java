package io.github.rikkakawaii0612.mutsumi.api.contact;

import io.github.rikkakawaii0612.mutsumi.api.contact.handler.MessageHandler;

public interface BotBus {
    void addMessageHandler(MessageHandler handler);

    Group getGroup(long id);
}
