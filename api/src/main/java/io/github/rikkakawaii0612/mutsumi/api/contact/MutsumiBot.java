package io.github.rikkakawaii0612.mutsumi.api.contact;

import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;

/**
 * MutsumiBot 的实例接口，包含 Bot 的基本功能（发送消息、监听消息等）
 */
public interface MutsumiBot {
    void sendMessage(long group, Message message);

    Group getGroup(long id);
}
