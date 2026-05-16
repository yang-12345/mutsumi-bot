package io.github.rikkakawaii0612.mutsumi.impl;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class LocalMutsumiBot implements MutsumiBot {
    public static final Logger LOGGER = LoggerFactory.getLogger("MutsumiLocal");

    public static final Member OWNER = () -> 1L;
    public static final Group GROUP = new Group() {
        @Override
        public long getId() {
            return 1L;
        }

        @Override
        public List<Member> getMembers() {
            return List.of(OWNER);
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
        return id == 1 ? GROUP : null;
    }

    @Override
    public void uploadFile(long group, String fileName, byte[] data) {
        LOGGER.info("Uploaded file '{}': {}", fileName, Integer.toHexString(Arrays.hashCode(data)));
        Path path = Path.of("output");
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            try (FileOutputStream os = new FileOutputStream(Path.of("output", fileName).toFile())) {
                os.write(data);
            }
        } catch (IOException e) {
            LOGGER.info("Failed to write file '{}': ", fileName, e);
        }
    }
}
