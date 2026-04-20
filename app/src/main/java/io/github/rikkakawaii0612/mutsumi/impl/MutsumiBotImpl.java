package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.At;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.MessageChain;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Text;
import io.github.rikkakawaii0612.mutsumi.api.handler.MessageHandler;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceNotFoundException;
import io.github.rikkakawaii0612.mutsumi.service.ModuleManager;
import io.github.rikkakawaii0612.mutsumi.util.BotLogger;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.BotIsBeingMutedException;
import net.mamoe.mirai.contact.MessageTooLargeException;
import net.mamoe.mirai.event.events.EventCancelledException;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.BotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mrxiaom.overflow.BotBuilder;

import java.util.List;

public class MutsumiBotImpl implements MutsumiBot {
    public static final Logger LOGGER = LoggerFactory.getLogger("Mutsumi");

    private final ModuleManager moduleManager;
    private final Bot bot;

    public MutsumiBotImpl(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.bot = BotBuilder.reversed(8080)
                .token("")
                .withBotConfiguration(() -> new BotConfiguration() {{
                    this.setBotLoggerSupplier(_ -> new BotLogger());
                }})
                .connect();
        if (this.bot != null) {
            this.bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, this::onMessageReceived);
        }
    }

    private void onMessageReceived(GroupMessageEvent event) {
        Group group = this.getGroup(event.getGroup().getId());
        long l = event.getSender().getId();
        Member sender = () -> l;
        MessageChain.Builder builder = new MessageChain.Builder();
        event.getMessage().forEach(singleMessage -> {
            switch (singleMessage) {
                case PlainText v -> builder.append(Message.text(v.contentToString()));
                case At v -> builder.append(Message.at(v.getTarget()));
                default -> {}
            }
        });
        MessageChain message = builder.build();

        List<MessageHandler> handlers = this.moduleManager.getExtensions(MessageHandler.class);
        handlers.forEach(handler -> {
            try {
                handler.handleMessage(this, group, sender, message);
            } catch (ServiceNotFoundException e) {
                LOGGER.error("Service missing while handling message: ", e);
            }
        });
    }

    @Override
    public void sendMessage(long id, Message message) {
        net.mamoe.mirai.contact.Group group = this.bot.getGroup(id);
        if (group == null) {
            LOGGER.warn("Try to send message in invalid group {}!", id);
            return;
        }

        MessageChainBuilder builder = new MessageChainBuilder();
        message.visit(m -> {
            if (m instanceof At at) {
                builder.append(new net.mamoe.mirai.message.data.At(at.getTarget()));
            } else if (m instanceof Text text) {
                builder.append(text.getText());
            } else {
                builder.append(m.asString());
            }
        });

        net.mamoe.mirai.message.data.MessageChain messageChain = builder.asMessageChain();
        if (MessageUtils.isContentEmpty(messageChain)) {
            return;
        }

        try {
            group.sendMessage(messageChain);
        } catch (EventCancelledException _) {
            LOGGER.info("Event cancelled while sending message in group {}.", id);
        } catch (BotIsBeingMutedException _) {
            LOGGER.info("Trying to send message in group {} but found Mutsumi muted.", id);
        } catch (MessageTooLargeException e) {
            LOGGER.warn("Trying to send too large message: ", e);
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception occurred while sending message: ", e);
        }
    }

    @Override
    public Group getGroup(long id) {
        net.mamoe.mirai.contact.Group group = this.bot.getGroup(id);
        if (group != null) {
            return new Group() {
                @Override
                public long getId() {
                    return id;
                }

                @Override
                public List<Member> getMembers() {
                    return List.of();
                }
            };
        }

        return null;
    }
}
