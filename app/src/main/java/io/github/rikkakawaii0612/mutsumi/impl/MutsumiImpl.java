package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.loader.MutsumiServiceLoader;

public final class MutsumiImpl implements Mutsumi {
    private final BotBusImpl botBus;
    private final MutsumiServiceLoader serviceLoader;

    public MutsumiImpl() {
        this.botBus = new BotBusImpl(this);
        this.serviceLoader = new MutsumiServiceLoader(this);
    }

    public void runBots() {
        this.botBus.start();
    }

    @Override
    public BotBusImpl getBotBus() {
        return this.botBus;
    }

    // 内部方法, 服务不应调用
    public MutsumiServiceLoader getServiceLoader() {
        return this.serviceLoader;
    }
}
