package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.*;
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

    private final Bot bot;

    public MutsumiBotImpl(Bot bot) {
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
        // 用于辅助添加自称前后的空格
        boolean[] addSpace = new boolean[1];
        message.visit(m -> {
            String str = m.asString();
            if (addSpace[0] && !str.isEmpty()) {
                addSpace[0] = false;
                if (Character.isLetterOrDigit(str.charAt(0))) {
                    builder.add(" ");
                }
            }
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
                case Autonym autonym -> {
                    String a = autonym.asString();
                    if (a.isEmpty()) {
                        break;
                    }
                    String s = builder.asMessageChain().contentToString();
                    char c = s.charAt(s.length() - 1);
                    if (shouldAddSpace(a.charAt(0)) && Character.isLetterOrDigit(c)) {
                        builder.add(" ");
                    }
                    builder.add(a);
                    if (shouldAddSpace(a.charAt(a.length() - 1))) {
                        addSpace[0] = true;
                    }
                }
                default -> builder.append(str);
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

    /**
     * 用于检查自称消息类型前后是否应该加空格.
     */
    private static boolean shouldAddSpace(char c) {
        if (!Character.isLetterOrDigit(c)) {
            return false;
        }
        Character.UnicodeScript script = Character.UnicodeScript.of(c);
        return script != Character.UnicodeScript.HAN
                && script != Character.UnicodeScript.HIRAGANA
                && script != Character.UnicodeScript.KATAKANA;
    }
}
