package io.github.rikkakawaii0612.mutsumi.osuImage;

import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Image;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.GreekBackgroundGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OsuImageService implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuImageService");
    private final Object lock = new Object();

    private final Map<Long, Map<Long, GreekInfo>> listeningInfos = new ConcurrentHashMap<>();

    @Override
    public void load(String id, ServiceLookup lookup) {
        lookup.getMutsumi().getBotBus().addMessageHandler(this::onHandleMessage);
        loadFont("assets/fonts/Torus-Light.otf");
        loadFont("assets/fonts/Torus-Regular.otf");
        loadFont("assets/fonts/Torus-SemiBold.otf");
    }

    @Override
    public void unload() {
    }


    public void onHandleMessage(MutsumiBot bot, Group group, Member sender, Message message) {
        List<Image> images = new ArrayList<>();
        message.visit(m -> {
            if (m instanceof Image i) {
                images.add(i);
            }
        });
        if (!images.isEmpty()) {
            if (images.size() == 1) {
                this.handleImage(bot, group, sender, images.getFirst().getData());
            }
            return; // 有图片就不可能匹配指令了
        }

        String str = message.asString().trim();
        if (!str.startsWith("!") && !str.startsWith("！") && !str.startsWith("/")) {
            return;
        }

        synchronized (this.lock) {
            String command = str.substring(1);
            CommandMatcher.Result greek = CommandMatchers.GREEK.matches(command);
            if (greek.doesMatches()) {
                this.commandGreek(bot, group, sender, greek);
            }
        }
    }

    private void handleImage(MutsumiBot bot, Group group, Member sender, byte[] image) {
        long groupId = group.getId();
        long senderId = sender.getId();
        if (!this.listeningInfos.containsKey(groupId)) {
            return;
        }

        Map<Long, GreekInfo> map = this.listeningInfos.get(groupId);
        if (!map.containsKey(senderId)) {
            return;
        }

        GreekInfo greekInfo = map.get(senderId);
        long l = System.nanoTime();
        if (l > greekInfo.time + 30000000000L) {
            return;
        }

        map.remove(senderId);
        byte[] result = GreekBackgroundGenerator.addGreek(image, greekInfo.greek);
        bot.sendMessage(groupId, Message.at(senderId).append(Message.image(result)));
    }

    private void commandGreek(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result result) {
        long groupId = group.getId();
        long senderId = sender.getId();

        String greek = result.getValue("greek", String.class);
        byte[] greekImage = GreekBackgroundGenerator.getGreek(greek);
        if (greekImage == null) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 暂时没有这个希腊字母。"));
            return;
        }

        if (!this.listeningInfos.containsKey(groupId)) {
            this.listeningInfos.put(groupId, new ConcurrentHashMap<>());
        }

        this.listeningInfos.get(groupId).put(senderId, new GreekInfo(System.nanoTime(), greekImage));
        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 已发送希腊字母 " + greek + " 图片生成请求！请在 30 秒内发送你要生成的图片。" +
                        "\n超时的请求会被自动忽略。"));
    }

    private static void loadFont(String path) {
        try (InputStream fontStream = OsuImageService.class.getClassLoader().getResourceAsStream(path)) {
            if (fontStream == null) {
                LOGGER.warn("Font file not found at: {}", path);
                return;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            LOGGER.warn("Failed to load font file at '{}': ", path, e);
        }
    }

    record GreekInfo(long time, byte[] greek) {
    }
}
