package io.github.rikkakawaii0612.mutsumi.api.contact.handler;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;

public interface MessageHandler {
    void handleMessage(MutsumiBot bot, Group group, Member sender, Message message);
}