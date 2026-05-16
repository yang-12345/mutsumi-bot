package io.github.rikkakawaii0612.mutsumi.api.contact.message;

import io.github.rikkakawaii0612.mutsumi.api.MutsumiProvider;

public class Autonym implements SingleMessage {
    /**
     * 自称消息类型在控制台与群聊的输出实际上不一样:
     * 控制台为原始自称, 而群聊输出会根据情况在前后加空格。
     */
    @Override
    public String asString() {
        return MutsumiProvider.getInstance().getName();
    }
}
