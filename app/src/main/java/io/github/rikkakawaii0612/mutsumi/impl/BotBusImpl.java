package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.api.contact.BotBus;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.handler.MessageHandler;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.At;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.MessageChain;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.PlainText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class BotBusImpl implements BotBus {
    private static final Logger LOGGER = LoggerFactory.getLogger("Mutsumi");

    private final Mutsumi mutsumi;
    private final Map<Long, MutsumiBot> idsToBots = new ConcurrentHashMap<>();
    // 本地控制台 Bot
    private final LocalMutsumiBot localBot = new LocalMutsumiBot();
    private final List<MessageHandler> messageHandlers = new ArrayList<>();
    private final Random random = new Random();

    // 防止遍历 messageHandlers 的时候增删元素
    private final Object lock = new Object();

    public BotBusImpl(Mutsumi mutsumi) {
        this.mutsumi = mutsumi;
    }

    public void start() {
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            Bot bot = event.getBot();
            MutsumiBotImpl mutsumiBot = new MutsumiBotImpl(this.mutsumi, bot);
            this.idsToBots.put(bot.getId(), mutsumiBot);
            LOGGER.info("[Online] Bot {} connected", bot.getId());
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(BotOfflineEvent.class, event -> {
            Bot bot = event.getBot();
            this.idsToBots.remove(bot.getId());
            LOGGER.info("[Offline] Bot {} disconnected", bot.getId());
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            long groupId = event.getGroup().getId();
            List<MutsumiBot> botsInGroup = this.idsToBots.values().stream()
                    .filter(bot -> bot.getGroup(groupId) != null)
                    .toList();
            if (botsInGroup.isEmpty()) {
                LOGGER.warn("Received message but no bots are in the group {}, this shouldn't happen!", groupId);
                return;
            }

            // 消息分流: 随机选择一个 Bot 处理消息
            MutsumiBot mutsumiBot = botsInGroup.get(this.random.nextInt(botsInGroup.size()));
            Group group = mutsumiBot.getGroup(groupId);
            long sender = event.getSender().getId();
            Message message = convertMessage(event.getMessage());

            this.handleMessage(mutsumiBot, group, () -> sender, message);
        });
    }

    private static Message convertMessage(net.mamoe.mirai.message.data.MessageChain message) {
        MessageChain.Builder builder = new MessageChain.Builder();
        message.forEach(singleMessage -> {
            switch (singleMessage) {
                case PlainText v -> builder.append(Message.text(v.contentToString()));
                case At v -> builder.append(Message.at(v.getTarget()));
                default -> {}
            }
        });
        return builder.build();
    }

    @Override
    public void addMessageHandler(MessageHandler handler) {
        synchronized (this.lock) {
            this.messageHandlers.add(handler);
        }
    }

    // 不希望其它服务随便调用这个方法
    public void clearMessageHandlers() {
        synchronized (this.lock) {
            this.messageHandlers.clear();
        }
    }

    public void sendToLocalBot(String text) {
        Group group = this.localBot.getGroup();
        Member member = this.localBot.getOwner();
        Message message = Message.text(text);

        this.handleMessage(this.localBot, group, member, message);
    }

    private void handleMessage(MutsumiBot mutsumiBot, Group group, Member sender, Message message) {
        synchronized (this.lock) {
            this.messageHandlers.forEach(handler -> {
                // 异步处理消息
                CompletableFuture.runAsync(() -> {
                    try {
                        handler.handleMessage(mutsumiBot, group, sender, message);
                    } catch (Exception e) {
                        LOGGER.error("Caught exception while handling message: ", e);
                    }
                });
            });
        }
    }

    @Override
    public Group getGroup(long id) {
        for (MutsumiBot bot : this.idsToBots.values()) {
            Group group = bot.getGroup(id);
            if (group != null) {
                return group;
            }
        }

        return null;
    }
}
