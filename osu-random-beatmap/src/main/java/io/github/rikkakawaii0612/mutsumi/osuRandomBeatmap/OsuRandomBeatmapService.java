package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.util.Pair;
import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;

public class OsuRandomBeatmapService implements Service {
    private Mutsumi mutsumi;
    private final Object lock = new Object();

    @Override
    public void load(String id, ServiceLookup lookup) {
        this.mutsumi = lookup.getMutsumi();
        lookup.getMutsumi().getBotBus().addMessageHandler(this::onHandleMessage);
    }

    @Override
    public void unload() {
    }


    public void onHandleMessage(MutsumiBot bot, Group group, Member sender, Message message) {
        String m = message.asString();
        String str = m.trim();
        if (!str.startsWith("!") && !str.startsWith("！") && !str.startsWith("/")) {
            return;
        }

        synchronized (this.lock) {
            String command = str.substring(1);
            CommandMatcher.Result jack = CommandMatchers.JACK.matches(command);
            if (jack.doesMatches()) {
                this.commandJack(bot, group, sender, jack);
                return;
            }

            CommandMatcher.Result speed = CommandMatchers.SPEED.matches(command);
            if (speed.doesMatches()) {
                this.commandSpeed(bot, group, sender, speed);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void commandJack(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        double bpm = params.getValue("bpm", Double.class);
        Pair<Integer, Integer> time = params.getValue("time", Pair.class);
        String config = params.getValue("config", String.class);

        if (this.checkInvalidBpmAndTime(bot, group, sender, bpm, time)) {
            return;
        }

        long groupId = group.getId();
        long senderId = sender.getId();

        if (!config.matches("[01234]+")) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 请输入正确的密度配置，应该为 0, 1, 2, 3, 4 组成的序列。"));
            return;
        }

        if (config.matches("0+")) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 全是 0，打集贸呢？"));
            return;
        }

        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 正在接入 " + this.mutsumi.getName() + " AI……"));

        int[] arr = new int[config.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Character.getNumericValue(config.charAt(i));
        }

        int min = time.left(), max = time.right();

        JackGenerator generator = new JackGenerator(min, bpm, arr);
        BeatmapContent beatmapContent = Generators.generate(max, bpm, 8.0D, 8.0D, generator);
        byte[] data = Generators.toOsz(max, beatmapContent);

        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 顶级谱师 " + this.mutsumi.getName() + " 生成谱面完毕。正在上传……"));
        bot.uploadFile(groupId,
                "beatmap-jack-" + config + "-" + Integer.toHexString(beatmapContent.hashCode()) + ".osz",
                data);
    }

    @SuppressWarnings("unchecked")
    private void commandSpeed(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        double bpm = params.getValue("bpm", Double.class);
        Pair<Integer, Integer> time = params.getValue("time", Pair.class);
        String config = params.getValue("config", String.class);
        double bumpChance = params.getValue("bumpChance", Double.class);

        if (this.checkInvalidBpmAndTime(bot, group, sender, bpm, time)) {
            return;
        }

        long groupId = group.getId();
        long senderId = sender.getId();

        if (bumpChance < 0.0D || bumpChance > 1.0D) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 子弹概率必须在[0,1]以内啦！"));
            return;
        }

        if (!config.matches("[0123]+")) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 请输入正确的密度配置，应该为 0, 1, 2, 3 组成的序列。"));
            return;
        }

        if (config.matches("0+")) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 全是 0，打集贸呢？"));
            return;
        }

        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 正在接入 " + this.mutsumi.getName() + " AI……"));

        int[] arr = new int[config.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Character.getNumericValue(config.charAt(i));
        }

        int min = time.left(), max = time.right();

        SpeedGenerator generator = new SpeedGenerator(min, bpm, bumpChance, arr);
        BeatmapContent beatmapContent = Generators.generate(max, bpm, 8.0D, 8.0D, generator);
        byte[] data = Generators.toOsz(max, beatmapContent);

        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 顶级谱师 " + this.mutsumi.getName() + " 生成谱面完毕。正在上传……"));
        bot.uploadFile(groupId,
                "beatmap-speed-" + config + "-" + Integer.toHexString(beatmapContent.hashCode()) + ".osz",
                data);
    }

    private boolean checkInvalidBpmAndTime(MutsumiBot bot,
                                           Group group,
                                           Member sender,
                                           double bpm,
                                           Pair<Integer, Integer> time) {
        long groupId = group.getId();
        long senderId = sender.getId();

        if (bpm <= 0.0D) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 负数的 BPM……？这是在时间倒流吗？"));
            return true;
        }
        if (Double.isInfinite(bpm)) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 无穷大的 BPM 都来了……？"));
            return true;
        }
        if (bpm > 1000.0D) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 唔……BPM 这么高，" + this.mutsumi.getName() + " 会被弄坏的……"));
            return true;
        }
        if (Double.isNaN(bpm)) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 你卡 bug 呢？"));
            return true;
        }

        int min = time.left(), max = time.right();
        if (min >= max) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 请输入一个正常点的时间区间。"));
            return true;
        }
        if (min < 0) {
            bot.sendMessage(groupId, Message.at(senderId).append(" ……负的时间点？"));
            return true;
        }
        if (max > 600000) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 唔……一次时长这么长，" + this.mutsumi.getName() + " 会被弄坏的……"));
            return true;
        }

        return false;
    }
}
