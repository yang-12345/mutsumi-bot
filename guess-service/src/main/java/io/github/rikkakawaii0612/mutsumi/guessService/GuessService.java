package io.github.rikkakawaii0612.mutsumi.guessService;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.guessService.image.Images;
import io.github.rikkakawaii0612.mutsumi.osuApi.OsuApiService;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuessService implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("GuessService");

    private OsuApiService osuApiService;

    private final Map<Long, GameInfo> gameInfos = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public GuessService() {
    }

    @Override
    public void load(String id, ServiceLookup lookup) {
        this.osuApiService = (OsuApiService) lookup.getService("osu-api").service();
        JsonNode config = lookup.getConfig().getOrCreate(id);
        AliasSystem.loadConfig(config);
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

        // 同步锁, 防止同时操作带来的诡异问题
        synchronized (this.lock) {
            String command = str.substring(1);
            CommandMatcher.Result letter = CommandMatchers.LETTER.matches(command);
            if (letter.doesMatches()) {
                this.commandLetter(bot, group, sender, letter);
                return;
            }

            CommandMatcher.Result guess = CommandMatchers.GUESS.matches(command);
            if (guess.doesMatches()) {
                this.commandGuess(bot, group, sender, guess);
                return;
            }

            CommandMatcher.Result open = CommandMatchers.OPEN.matches(command);
            if (open.doesMatches()) {
                this.commandOpen(bot, group, sender, open);
                return;
            }

            CommandMatcher.Result answer = CommandMatchers.ANSWER.matches(command);
            if (answer.doesMatches()) {
                this.commandAnswer(bot, group, sender, answer);
            }
        }
    }

    private void commandLetter(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        long groupId = group.getId();
        if (this.gameInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("当前还有正在进行的开字母游戏。你可以用 /answer 来强制结束游戏。"));
            return;
        }

        Integer integer = params.getValue("count", Integer.class);
        int size = integer != null ? integer : 10;
        if (size <= 0) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("那你猜什么啊。"));
            return;
        } else if (size > 20) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("猜这么多，").appendAutonym().append("会受不了的啦！"));
            return;
        }

        String userParam = params.getValue("user", String.class);
        PlayMode playMode = params.getOrDefault("playMode", PlayMode.class, PlayMode.MANIA);
        Optional<User> optional = this.osuApiService.getUser(userParam, playMode);
        if (optional.isEmpty()) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .appendAutonym().append("找不到用户。你是不是输错了……？"));
            return;
        }

        User user = optional.get();
        bot.sendMessage(groupId, Message.atThen(sender.getId())
                .appendAutonym().append("正在查找用户 ").append(user.username).append(" 的最好成绩。稍等。"));

        Optional<List<Score>> optional2 = this.osuApiService.getBestScores(user.id, PlayMode.MANIA);
        if (optional2.isEmpty()) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .appendAutonym().append("获取用户 ").append(user.id).append(" 最好成绩时发生错误。")
                    .append("\n……是不是应该报告给开发者？"));
            return;
        }
        List<Score> bestScores = optional2.get();

        Random random = new Random();
        List<Beatmap> beatmaps = new ArrayList<>();

        for (int i = 0; i < size && !bestScores.isEmpty(); ) {
            Score score = bestScores.remove(random.nextInt(bestScores.size()));
            Beatmap beatmap = score.beatmap;
            beatmap.beatmapset = score.beatmapset;
            boolean foundSimilar = false;
            for (Beatmap o : beatmaps) {
                if (beatmap.beatmapset.title.equals(o.beatmapset.title)) {
                    foundSimilar = true;
                    break;
                }
            }
            if (foundSimilar) {
                continue;
            }
            beatmaps.add(beatmap);
            i++;
        }

        if (beatmaps.isEmpty()) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .appendAutonym().append("找不到用户 ").append(user.username).append(" 的最好成绩。"));
            return;
        }

        GameInfo gameInfo = new GameInfo(user, beatmaps, playMode, true, false);
        this.gameInfos.put(groupId, gameInfo);
        bot.sendMessage(groupId, Message.atThen(sender.getId())
                .appendAutonym().append("从用户 ").append(user.username).append(" 的 ")
                .append(playMode).append(" 模式中选取了 ").append(beatmaps.size()).append(" 个成绩。开始猜歌！"));

        this.sendGameInfo(bot, group, gameInfo);
    }

    private void commandGuess(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        long groupId = group.getId();
        if (!this.gameInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("当前没有正在进行的开字母游戏。"));
            return;
        }

        GameInfo gameInfo = this.gameInfos.get(groupId);
        String song = params.getValue("song", String.class);
        int index = params.getValue("index", Integer.class) - 1;

        if (index < 0 || index >= gameInfo.getSongCount()) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("……有这个位置的谱面吗？"));
            return;
        }

        if (gameInfo.isDecrypted(index)) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("可是这个谱面已经结束了。"));
        } else if (gameInfo.guess(index, song)) {
            Beatmapset beatmapset = gameInfo.getBeatmap(index).beatmapset;
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("猜中谱面：").append(beatmapset.artistUnicode)
                    .append(" - ").append(beatmapset.titleUnicode));
            this.sendGameInfo(bot, group, gameInfo);
            this.checkFinished(bot, group, gameInfo);
        } else {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("不是这个谱面诶……是不是输错了？"));
        }
    }

    private void commandOpen(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        long groupId = group.getId();
        if (!this.gameInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("当前没有正在进行的开字母游戏。"));
            return;
        }

        GameInfo gameInfo = this.gameInfos.get(groupId);
        char character = params.getValue("char", Character.class);
        if (gameInfo.open(character)) {
            this.sendGameInfo(bot, group, gameInfo);
        } else {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("字母 '").append(character).append("' 已经开过了。你再好好看看呢？"));
        }
    }

    private void commandAnswer(MutsumiBot bot, Group group, Member sender, CommandMatcher.Result params) {
        long groupId = group.getId();
        if (!this.gameInfos.containsKey(groupId)) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("当前没有正在进行的开字母游戏。"));
            return;
        }

        GameInfo gameInfo = this.gameInfos.get(groupId);
        Integer index = params.getValue("index", Integer.class);
        if (index == null) {
            bot.sendMessage(groupId, Message.atThen(sender.getId())
                    .append("唔，已经尽力了吗……那就给你看答案好了"));
            gameInfo.decryptAll();
            this.sendGameInfo(bot, group, gameInfo);
            this.checkFinished(bot, group, gameInfo);
        } else {
            if (index <= 0 && index > gameInfo.getSongCount()) {
                bot.sendMessage(groupId, Message.atThen(sender.getId())
                        .append("……有这个位置的谱面吗？"));
            } else {
                bot.sendMessage(groupId, Message.atThen(sender.getId())
                        .append("唔，猜不出来这个吗……那就给你看答案好了"));
                gameInfo.decrypt(index - 1);
                this.sendGameInfo(bot, group, gameInfo);
                this.checkFinished(bot, group, gameInfo);
            }
        }
    }

    private void checkFinished(MutsumiBot bot, Group group, GameInfo gameInfo) {
        if (gameInfo.isFinished()) {
            long groupId = group.getId();
            bot.sendMessage(groupId, Message.text("开字母游戏已结束。"));
            this.gameInfos.remove(groupId);
        }
    }

    private void sendGameInfo(MutsumiBot bot, Group group, GameInfo gameInfo) {
        bot.sendMessage(group.getId(), Message.image(Images.renderGameInfo(gameInfo)));
    }
}
