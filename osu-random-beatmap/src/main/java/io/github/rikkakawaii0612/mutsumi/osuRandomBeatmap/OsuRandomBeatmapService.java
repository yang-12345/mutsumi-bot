package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import io.github.rikkakawaii0612.mutsumi.api.Mutsumi;
import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap.beatmap.Generator;
import io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap.beatmap.Generators;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OsuRandomBeatmapService implements Service {
    private Mutsumi mutsumi;
    private final Map<Long, Generator> generatorInfos = new ConcurrentHashMap<>();
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
            CommandMatcher.Result finish = CommandMatchers.FINISH.matches(command);
            if (finish.doesMatches()) {
                this.commandFinish(bot, group, sender);
                return;
            }

            CommandMatcher.Result cancel = CommandMatchers.CANCEL.matches(command);
            if (cancel.doesMatches()) {
                this.commandCancel(bot, group, sender);
                return;
            }

            CommandMatcher.Result selectType = CommandMatchers.SELECT_TYPE.matches(command);
            if (selectType.doesMatches()) {
                commandSelectType(bot, group, sender, selectType);
                return;
            }

            CommandMatcher.Result setParam = CommandMatchers.SET_PARAM.matches(command);
            if (setParam.doesMatches()) {
                this.commandSetParameter(bot, group, sender, setParam);
            }
        }
    }

    private void commandSelectType(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        long groupId = group.getId();
        long senderId = sender.getId();

        if (this.generatorInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(" 当前还有正在设置的谱面生成器。你可以使用 '/randBeatmap cancel' 来结束当前设置。"));
            return;
        }

        String type = params.getValue("type", String.class);
        Generator generator = Generators.createGenerator(type);
        if (generator == null) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 没有该类型的生成器。"));
            return;
        }

        this.generatorInfos.put(groupId, generator);
        bot.sendMessage(groupId, Message.at(senderId).append(" 创建成功！使用 '/randBeatmap set <键> <值>' 来设置参数。"));
        this.sendGeneratorInfo(bot, group, sender);
    }

    private void commandSetParameter(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        long groupId = group.getId();
        long senderId = sender.getId();

        if (!this.generatorInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 当前没有正在设置的谱面生成器。"));
            return;
        }

        Generator generator = this.generatorInfos.get(groupId);
        String key = params.getValue("key", String.class);
        String value = params.getValue("value", String.class);
        try {
            generator.setParameter(key, value);
        } catch (Exception e) {
            bot.sendMessage(groupId, Message.at(senderId)
                    .append(String.format(" " + e.getLocalizedMessage(), this.mutsumi.getName())));
            return;
        }
        this.sendGeneratorInfo(bot, group, sender);
    }

    private void commandFinish(MutsumiBot bot, Group group, Member sender) {
        long groupId = group.getId();
        long senderId = sender.getId();

        if (!this.generatorInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 当前没有正在设置的谱面生成器。"));
            return;
        }

        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 参数输入完毕。正在接入 " + this.mutsumi.getName() + " AI 进行创作……"));

        Generator generator = this.generatorInfos.remove(groupId);
        byte[] data = generator.generate();

        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 顶级谱师 " + this.mutsumi.getName() + " 创作谱面完毕。正在上传……"));
        bot.uploadFile(groupId,
                "beatmap-" + Integer.toHexString(Arrays.hashCode(data)) + ".osz",
                data);
    }

    private void commandCancel(MutsumiBot bot, Group group, Member sender) {
        long groupId = group.getId();
        long senderId = sender.getId();

        if (!this.generatorInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 当前没有正在设置的谱面生成器。"));
            return;
        }

        this.generatorInfos.remove(groupId);
        bot.sendMessage(groupId, Message.at(senderId)
                .append(" 已取消当前正在设置的谱面生成计划。"));
    }

    private void sendGeneratorInfo(MutsumiBot bot, Group group, Member sender) {
        long groupId = group.getId();
        long senderId = sender.getId();

        if (!this.generatorInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.at(senderId).append(" 当前没有正在设置的谱面生成器。"));
            return;
        }

        Generator generator = this.generatorInfos.get(groupId);
        StringBuilder builder = new StringBuilder();
        builder.append(" 类型：").append(generator.getName());
        for (String str : generator.getInfo()) {
            builder.append('\n').append(str);
        }
        builder.append("\n准备好了就使用 '/randBeatmap ok' 来生成谱面。");
        bot.sendMessage(groupId, Message.at(senderId).append(builder.toString()));
    }
}
