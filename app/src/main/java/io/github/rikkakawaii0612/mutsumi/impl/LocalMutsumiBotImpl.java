package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.handler.MessageHandler;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceNotFoundException;
import io.github.rikkakawaii0612.mutsumi.service.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocalMutsumiBotImpl implements MutsumiBot {
    public static final Logger LOGGER = LoggerFactory.getLogger("MutsumiLocal");

    private final ModuleManager moduleManager;
    private final Member owner = () -> 1;
    private final Group group = new Group() {
        @Override
        public long getId() {
            return 1;
        }

        @Override
        public List<Member> getMembers() {
            return List.of(owner);
        }
    };

    public LocalMutsumiBotImpl(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public void receiveMessage(String str) {
        Message message = Message.text(str);

        List<MessageHandler> handlers = this.moduleManager.getExtensions(MessageHandler.class);

        handlers.forEach(handler -> CompletableFuture.runAsync(() -> {
            try {
                handler.handleMessage(this, group, owner, message);
            } catch (ServiceNotFoundException e) {
                LOGGER.error("Service missing while handling message: ", e);
            } catch (Exception e) {
                LOGGER.error("Caught exception while handling message: ", e);
            }
        }));
    }

    @Override
    public void sendMessage(long id, Message message) {
        LOGGER.info(message.asString());
    }

    @Override
    public Group getGroup(long id) {
        return id == 1 ? this.group : null;
    }
}
