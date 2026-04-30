package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalMutsumiBot implements MutsumiBot {
    public static final Logger LOGGER = LoggerFactory.getLogger("MutsumiLocal");

    private final Member owner = () -> 1L;
    private final Group group = new Group() {
        @Override
        public long getId() {
            return 1L;
        }

        @Override
        public List<Member> getMembers() {
            return List.of(owner);
        }
    };

    public LocalMutsumiBot() {
    }

    @Override
    public void sendMessage(long id, Message message) {
        LOGGER.info(message.asString());
    }

    @Override
    public Group getGroup(long id) {
        return id == 1 ? this.group : null;
    }

    public Group getGroup() {
        return this.group;
    }

    public Member getOwner() {
        return this.owner;
    }
}
