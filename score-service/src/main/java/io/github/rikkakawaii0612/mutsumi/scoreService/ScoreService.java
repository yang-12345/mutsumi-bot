package io.github.rikkakawaii0612.mutsumi.scoreService;

import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.util.MutsumiUtils;
import io.github.rikkakawaii0612.mutsumi.api.util.Pair;
import io.github.rikkakawaii0612.mutsumi.osuApi.OsuApiService;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class ScoreService implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("ScoreService");

    private OsuApiService osuApiService;

    public ScoreService() {
    }

    @Override
    public void load(String id, ServiceLookup lookup) {
        this.osuApiService = (OsuApiService) lookup.getService("osu-api").service();
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

        if (str.substring(1).startsWith("bp ")) {
            String param = str.substring(4);
            Optional<User> optional = this.osuApiService.getUser(param);

            if (optional.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(Message.text(" 没有找到用户名或ID为 " + param + " 的用户。")));
                return;
            }

            User user = optional.get();

            String username = user.username;
            long id = user.id;

            bot.sendMessage(group.getId(), Message.at(sender.getId())
                    .append(Message.text(" 正在查找用户 " + username + " 的bp200信息……" +
                            "\n这可能需要一些时间。")));

            Optional<List<Score>> optional2 = this.osuApiService.getBestScores(id, PlayMode.MANIA);
            if (optional2.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(Message.text(" 没有找到用户 " + username + " 的 BP200 成绩。")));
                return;
            }

            List<Score> scores = optional2.get();
            scores.sort(Comparator
                    .comparingDouble((Score score) -> score.pp)
                    .reversed());

            double pp = 0.0D, multiplier = 1.0D;
            for (Score score : scores) {
                pp += multiplier * score.pp;
                multiplier *= 0.95D;
            }

            // 计算 BonusPP 没有意义了
            StringBuilder builder = new StringBuilder(" 用户 " + username
                    + " 共找到 " + scores.size()
                    + " 个有效成绩，总pp（不包括BonusPP）：" + pp);

            int limit = Math.min(10, scores.size());
            for (int i = 0; i < limit; i++) {
                Beatmapset beatmapset = scores.get(i).beatmapset;
                builder.append("\n")
                        .append(beatmapset.artist)
                        .append(" - ")
                        .append(beatmapset.title)
                        .append(": ")
                        .append(scores.get(i).pp)
                        .append("pp");
            }

            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(builder.toString())));
        }

        if (str.substring(1).startsWith("bp7k ")) {
            String param = str.substring(6);
            Optional<User> optional = this.osuApiService.getUser(param);

            if (optional.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(Message.text(" 没有找到用户名或ID为 " + param + " 的用户。")));
                return;
            }

            User user = optional.get();

            String username = user.username;
            long id = user.id;

            bot.sendMessage(group.getId(), Message.at(sender.getId())
                    .append(Message.text(" 正在查找用户 " + username + " 的7k信息……" +
                            "\n这可能需要一些时间。")));

            Optional<List<BeatmapPlayCount>> optional2 = this.osuApiService.getAllPlayedBeatmaps(id);
            if (optional2.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(Message.text(" 没有找到用户 " + username + " 的 BP200 成绩。")));
                return;
            }

            List<BeatmapPlayCount> allPlayedBeatmaps = optional2.get();

            // TODO: REMOVAL LOG
            LOGGER.info("allPlayedBeatmaps size: {}", allPlayedBeatmaps.size());

            List<List<Beatmap>> beatmapsToPost = new ArrayList<>();
            for (Iterator<BeatmapPlayCount> iterator = allPlayedBeatmaps.iterator(); iterator.hasNext(); ) {
                List<Beatmap> beatmapList = new ArrayList<>();

                // 收集50个谱面一组进行post
                while (beatmapList.size() < 50 && iterator.hasNext()) {
                    BeatmapPlayCount beatmapPlayCount = iterator.next();
                    Beatmap beatmap = beatmapPlayCount.beatmap;

                    PlayMode playMode = beatmap.playMode;
                    if (playMode != PlayMode.MANIA) continue;

                    RankStatus status = beatmap.rankStatus;
                    if (status != RankStatus.RANKED) continue;

                    beatmapList.add(beatmap);
                }

                beatmapsToPost.add(beatmapList);
            }

            // 异步批量 post 获取谱面
            List<Supplier<Optional<List<Beatmap>>>> beatmapGetters = new ArrayList<>();

            for (List<Beatmap> list : beatmapsToPost) {
                List<Long> ids = list.stream()
                        .map(beatmap -> beatmap.id)
                        .toList();
                beatmapGetters.add(() -> this.osuApiService.getBeatmaps(ids));
            }

            List<Beatmap> beatmapsExtended = MutsumiUtils.getAsync(beatmapGetters).stream()
                    .flatMap(Optional::stream)
                    .flatMap(List::stream)
                    .toList();

            // 筛选7K谱面
            List<Beatmap> beatmaps = new ArrayList<>();
            for (Beatmap beatmap : beatmapsExtended) {
                if (beatmap.cs == 7) {
                    beatmaps.add(beatmap);
                }
            }

            // TODO: REMOVAL LOG
            LOGGER.info("beatmaps size: {}", beatmaps.size());

            // 异步批量 post 获取谱面成绩列表
            List<Supplier<Optional<List<Score>>>> scoreGetters = new ArrayList<>();
            for (Beatmap beatmap : beatmaps) {
                scoreGetters.add(() -> this.osuApiService.getUserBeatmapScores(id, beatmap.id));
            }
            List<Optional<List<Score>>> scores = MutsumiUtils.getAsync(scoreGetters);

            List<Pair<Beatmap, Score>> beatmapsToScores = new ArrayList<>();

            // 由于最好成绩不等于 PP 最高, 这里要比较出 PP 最高的成绩
            // 同时计算 Bonus PP 所需要的成绩总数 N
            int scoresCount = 0;
            for (int i = 0; i < scores.size(); i++) {
                Optional<List<Score>> optional3 = scores.get(i);
                if (optional3.isEmpty()) {
                    continue;
                }

                List<Score> scoreList = optional3.get();
                scoresCount += scoreList.size();
                Optional<Score> optional4 = scoreList.stream()
                        .max(Comparator.comparingDouble(score -> score.pp));

                if (optional4.isPresent()) {
                    beatmapsToScores.add(new Pair<>(beatmaps.get(i), optional4.get()));
                }
            }

            // TODO: REMOVAL LOG
            LOGGER.info("bestScores size: {}", beatmapsToScores.size());

            // 按 PP 从高到低排序
            beatmapsToScores.sort(Comparator
                    .comparingDouble((Pair<Beatmap, Score> value) -> value.right().pp)
                    .reversed());

            // 计算 BP 的总 PP, 加权求和
            double pp = 0.0D, multiplier = 1.0D;
            for (Pair<Beatmap, Score>
                    beatmapsetsToScore : beatmapsToScores) {
                pp += multiplier * beatmapsetsToScore.right().pp;
                multiplier *= 0.95D;
            }

            // 计算 Bonus PP = 416.6667 * (1 - 0.995 ^ N)
            // 中文 osu wiki 给的就是一坨, 公式也错, 给的 N 也错
            // 参见英文 osu wiki 对表现分的解释
            double bonusPp = 416.6667D * (1.0D - Math.exp(Math.log(0.995D) * Math.min(scoresCount, 1000)));

            // 纯文本输出, 后面要改图像输出
            StringBuilder builder = new StringBuilder(" 用户 " + username
                    + " 共找到 " + beatmapsToScores.size()
                    + " 个有效7k成绩，纯7k总pp：" + (pp + bonusPp)
                    + " (" + pp + " + " + bonusPp + ")");

            int limit = Math.min(30, beatmapsToScores.size());
            for (int i = 0; i < limit; i++) {
                Beatmap beatmap = beatmapsToScores.get(i).left();
                Beatmapset beatmapset = beatmap.beatmapset;
                builder.append("\n")
                        .append(beatmapset.artist)
                        .append(" - ")
                        .append(beatmapset.title)
                        .append(" (")
                        .append(beatmap.version)
                        .append("): ")
                        .append(beatmapsToScores.get(i).right().pp)
                        .append("pp");
            }

            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(builder.toString())));
        }
    }
}
