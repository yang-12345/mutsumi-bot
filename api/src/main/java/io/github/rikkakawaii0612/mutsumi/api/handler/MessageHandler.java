package io.github.rikkakawaii0612.mutsumi.api.handler;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;

@Extension
public interface MessageHandler extends ExtensionPoint {
    void handleMessage(MutsumiBot bot, Group group, Member sender, Message message);
}
