package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.At;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Image;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Text;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.BotIsBeingMutedException;
import net.mamoe.mirai.contact.MessageTooLargeException;
import net.mamoe.mirai.event.events.EventCancelledException;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.utils.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class MutsumiBotImpl implements MutsumiBot {
    public static final Logger LOGGER = LoggerFactory.getLogger("Mutsumi");

    private final Mutsumi mutsumi;
    private final Bot bot;

    public MutsumiBotImpl(Mutsumi mutsumi, Bot bot) {
        this.mutsumi = mutsumi;
        this.bot = bot;
    }

    @Override
    public void sendMessage(long id, Message message) {
        net.mamoe.mirai.contact.Group group = this.bot.getGroup(id);
        if (group == null) {
            LOGGER.warn("Try to send message in invalid group {}", id);
            return;
        }

        MessageChainBuilder builder = new MessageChainBuilder();
        message.visit(m -> {
            switch (m) {
                case At at -> builder.append(new net.mamoe.mirai.message.data.At(at.target()));
                case Text text -> builder.append(text.content());
                case Image image -> {
                    try (ByteArrayInputStream is = new ByteArrayInputStream(image.getData());
                         ExternalResource er = ExternalResource.create(is)) {
                        net.mamoe.mirai.message.data.Image imagex = group.uploadImage(er);
                        builder.append(imagex);
                    } catch (IOException e) {
                        LOGGER.warn("Failed to upload image {}: ", image.asString(), e);
                    }
                }
                default -> builder.append(m.asString());
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

    @Override
    public void uploadFile(long id, String fileName, byte[] data) {
        net.mamoe.mirai.contact.Group group = this.bot.getGroup(id);
        if (group == null) {
            LOGGER.warn("Try to send message in invalid group {}", id);
            return;
        }
        try (ExternalResource resource = ExternalResource.create(data, fileName)) {
            group.getFiles().uploadNewFile(fileName, resource);
        } catch (Exception e) {
            LOGGER.warn("Failed to upload file '{}' in group {}: ", fileName, id, e);
        }
    }
}
