package io.github.rikkakawaii0612.mutsumi.osuImage;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Image;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.util.Pair;
import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.GreekBackgroundGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OsuImageService implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuImageService");

    private Mutsumi mutsumi;

    private final Map<Long, Map<Long, GreekInfo>> listeningInfos = new ConcurrentHashMap<>();

    // omg 我 bot 因为希腊字母生成被封了!!! 故在此写一个限流.
    // 5 个令牌容量, 10 秒补充一个令牌
    private final Bucket limit = Bucket.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(5L)
                    .refillGreedy(1L, Duration.ofSeconds(10L))
                    .build())
            .build();

    // 达到限制后的回复也得限制频率, 在恢复容量之前不会再提示
    private boolean repliedLimitReached = false;

    // 因为要写限流 所以把锁加回来了
    private final Object lock = new Object();

    @Override
    public void load(String id, ServiceLookup lookup) {
        this.mutsumi = lookup.getMutsumi();
        this.mutsumi.getBotBus().addMessageHandler(this::onHandleMessage);
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
                this.handleImage(bot, group, sender, images.getFirst());
            }
            return; // 有图片就不可能匹配指令了
        }

        String str = message.asString().trim();
        if (!str.startsWith("!") && !str.startsWith("！") && !str.startsWith("/")) {
            return;
        }

        String command = str.substring(1);
        CommandMatcher.Result greek = CommandMatchers.GREEK.matches(command);
        if (greek.doesMatches()) {
            this.commandGreek(bot, group, sender, greek);
        }
    }

    private void handleImage(MutsumiBot bot, Group group, Member sender, Image image) {
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
        bot.sendMessage(groupId, Message.at(senderId)
                .append(" " + this.mutsumi.getName() + " 正在使用神秘技术处理图片……"));
        byte[] result = GreekBackgroundGenerator.addGreek(image.getData(),
                greekInfo.greek, greekInfo.chromaticAberration, greekInfo.glitch);
        bot.sendMessage(groupId, Message.at(senderId).append(Message.image(result)));
    }

    private void commandGreek(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result result) {
        long groupId = group.getId();
        long senderId = sender.getId();

        String greek = result.getValue("greek", String.class);
        Pair<byte[], GreekBackgroundGenerator.Param> pair = GreekBackgroundGenerator.getGreekInfo(greek);
        if (pair == null) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 暂时没有这个希腊字母。"));
            return;
        }

        Double chromaticAberration = result.getValue("chromaticAberration", Double.class);
        Double glitch = result.getValue("glitch", Double.class);
        GreekInfo greekInfo;
        long l = System.nanoTime();
        if (chromaticAberration != null && glitch != null) {
            if (chromaticAberration < 0.0D) {
                bot.sendMessage(groupId, Message.at(senderId).append("色差程度不能为负。"));
                return;
            }
            if (glitch < 0.0D) {
                bot.sendMessage(groupId, Message.at(senderId).append("故障程度不能为负。"));
                return;
            }
            if (chromaticAberration > 100.0D || glitch > 100.0D) {
                bot.sendMessage(groupId, Message.at(senderId).append("参数过大。"));
                return;
            }
            greekInfo = new GreekInfo(l, pair.left(), chromaticAberration, glitch);
        } else {
            GreekBackgroundGenerator.Param param = pair.right();
            greekInfo = new GreekInfo(l, pair.left(), param.chromaticAberration(), param.glitch());
        }

        // 限流这一块
        // 消耗令牌成功则取消已回复标记, 失败则在回复之后设置已回复标记
        synchronized (this.lock) {
            if (this.limit.tryConsume(1L)) {
                this.repliedLimitReached = false;
            } else {
                if (!this.repliedLimitReached) {
                    bot.sendMessage(groupId, Message.at(senderId)
                            .append("当前发送希腊字母的请求过多, 请先让 " + this.mutsumi.getName() + " 休息一会儿。"));
                }
                this.repliedLimitReached = true;
                return;
            }
        }

        if (!this.listeningInfos.containsKey(groupId)) {
            this.listeningInfos.put(groupId, new ConcurrentHashMap<>());
        }
        this.listeningInfos.get(groupId).put(senderId, greekInfo);

        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 已发送希腊字母 " + greek + " 图片生成请求！请在 30 秒内发送你要处理的图片。" +
                        "\n色差程度：" + greekInfo.chromaticAberration + " 故障程度：" + greekInfo.glitch +
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

    record GreekInfo(long time, byte[] greek, double chromaticAberration, double glitch) {
    }
}
