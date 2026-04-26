package io.github.rikkakawaii0612.mutsumi.scoreService;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.handler.MessageHandler;
import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceReference;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceRequest;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ListObjectData;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import io.github.rikkakawaii0612.mutsumi.api.util.Pair;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Extension
public class ScoreService extends Service implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("ScoreService");

    private ServiceReference osuApiService;

    public ScoreService() {
    }

    @Override
    public ObjectData call(ServiceRequest request) {
        return ObjectData.EMPTY;
    }

    @Override
    public void load() {
        this.osuApiService = this.getModule().getServiceLocator().getService("osu-api-base");
    }

    @Override
    public void unload() {
    }

    @Override
    public String getId() {
        return "guess-service";
    }

    @Override
    public void handleMessage(MutsumiBot bot, Group group, Member sender, Message message) {
        String m = message.asString();
        String str = m.trim();
        if (!str.startsWith("!") && !str.startsWith("！") && !str.startsWith("/")) {
            return;
        }

        if (str.substring(1).startsWith("bp ")) {
            String param = str.substring(4);
            ObjectData /*User*/ user = this.osuApiService.call(ServiceRequest.builder()
                    .header("service", "getUser")
                    .header("username", param)
                    .build());

            if (user.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(Message.text(" 没有找到用户名或ID为 " + param + " 的用户。")));
                return;
            }

            String username = user.getString("name");
            String id = user.getString("id");

            bot.sendMessage(group.getId(), Message.at(sender.getId())
                    .append(Message.text(" 正在查找用户 " + username + " 的bp200信息……" +
                            "\n这可能需要一些时间。")));

            ObjectData /*List<Score>*/ scores = this.osuApiService.call(ServiceRequest.builder()
                    .header("service", "getBestScores")
                    .header("id", id)
                    .header("mode", "mania")
                    .build());
            List<? extends ObjectData /*Score*/> list = ListObjectData.readAsMutable(scores);
            list.sort(Comparator
                    .comparingDouble((ObjectData /*Score*/ value)
                            -> value.getDouble("pp"))
                    .reversed());

            double pp = 0.0D, multiplier = 1.0D;
            for (ObjectData objectData : list) {
                pp += multiplier * objectData.getDouble("pp");
                multiplier *= 0.95D;
            }

            // 计算 BonusPP 没有意义了
            StringBuilder builder = new StringBuilder(" 用户 " + username
                    + " 共找到 " + list.size()
                    + " 个有效成绩，总pp（不包括BonusPP）：" + pp);

            int limit = Math.min(10, list.size());
            for (int i = 0; i < limit; i++) {
                ObjectData /*Beatmapset*/ beatmapset = list.get(i).get("beatmapset");
                builder.append("\n")
                        .append(beatmapset.getString("artist"))
                        .append(" - ")
                        .append(beatmapset.getString("title"))
                        .append(": ")
                        .append(list.get(i).getDouble("pp"))
                        .append("pp");
            }

            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(builder.toString())));
        }

        if (str.substring(1).startsWith("bp7k ")) {
            String param = str.substring(6);
            ObjectData /*User*/ user = this.osuApiService.call(ServiceRequest.builder()
                    .header("service", "getUser")
                    .header("username", param)
                    .build());

            if (user.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(Message.text(" 没有找到用户名或ID为 " + param + " 的用户。")));
                return;
            }

            String username = user.get("name").asString();
            String id = user.get("id").asString();

            bot.sendMessage(group.getId(), Message.at(sender.getId())
                    .append(Message.text(" 正在查找用户 " + username + " 的7k信息……" +
                            "\n这可能需要一些时间。")));

            List<? extends ObjectData /*BeatmapPlayCount*/>
                    allPlayedBeatmaps = ListObjectData.readAsMutable(
                            this.osuApiService.call(ServiceRequest.builder()
                                    .header("service", "getAllPlayedBeatmaps")
                                    .header("id", id)
                                    .build()));

            // TODO: REMOVAL LOG
            LOGGER.info("allPlayedBeatmaps size: {}", allPlayedBeatmaps.size());

            List<List<ObjectData /*Beatmap*/>> beatmapsToPost = new ArrayList<>();
            for (Iterator<? extends ObjectData> iterator = allPlayedBeatmaps.iterator(); iterator.hasNext(); ) {
                List<ObjectData /*Beatmap*/> beatmapList = new ArrayList<>();

                // 收集50个谱面一组进行post
                while (beatmapList.size() < 50 && iterator.hasNext()) {
                    ObjectData /*BeatmapPlayCount*/ beatmapPlayCount = iterator.next();
                    ObjectData /*Beatmap*/ beatmap = beatmapPlayCount.get("beatmap");

                    String playMode = beatmap.get("playMode").asString();
                    if (!"mania".equals(playMode)) continue;

                    String status = beatmap.get("rankStatus").asString();
                    if (!"ranked".equals(status)) continue;

                    beatmapList.add(beatmap);
                }

                beatmapsToPost.add(beatmapList);
            }

            // 异步批量 post 获取谱面
            List<ServiceRequest> requestsForBeatmaps = new ArrayList<>();

            for (List<ObjectData /*Beatmap*/> list : beatmapsToPost) {
                List<ObjectData /*Long*/> ids = list.stream()
                        .map(o -> o.get("id"))
                        .toList();
                requestsForBeatmaps.add(ServiceRequest.builder()
                        .header("service", "getBeatmaps")
                        .data("ids", ObjectData.of(ids))
                        .build());
            }

            List<ObjectData /*List<Beatmap>*/> beatmapsExtended = this.osuApiService.call(requestsForBeatmaps);

            // 筛选7K谱面
            List<ObjectData /*Beatmap*/> beatmaps = new ArrayList<>();
            for (ObjectData /*List<Beatmap>*/ o : beatmapsExtended) {
                for (ObjectData /*Beatmap*/ beatmap : ListObjectData.read(o)) {
                    if (beatmap.getInt("cs") == 7) {
                        beatmaps.add(beatmap);
                    }
                }
            }

            // TODO: REMOVAL LOG
            LOGGER.info("beatmaps size: {}", beatmaps.size());

            // 异步批量 post 获取谱面成绩列表
            List<ServiceRequest> requestsForScores = beatmaps.stream()
                    .map(beatmap -> ServiceRequest.builder()
                            .header("service", "getUserBeatmapScores")
                            .header("user", id)
                            .header("beatmap", beatmap.getString("id"))
                            .build())
                    .toList();
            List<ObjectData /*List<Score>*/> scores = this.osuApiService.call(requestsForScores);

            List<Pair<ObjectData /*Beatmap*/, ObjectData /*Score*/>>
                    beatmapsToScores = new ArrayList<>();

            // 由于最好成绩不等于 PP 最高, 这里要比较出 PP 最高的成绩
            // 同时计算 Bonus PP 所需要的成绩总数 N
            int scoresCount = 0;
            for (int i = 0; i < scores.size(); i++) {
                ObjectData /*Score*/ score = ObjectData.EMPTY;
                for (ObjectData /*Score*/ scorex : ListObjectData.read(scores.get(i))) {
                    scoresCount++;
                    if (score.isEmpty() || score.getDouble("pp") < scorex.getDouble("pp")) {
                        score = scorex;
                    }
                }

                if (!score.isEmpty()) {
                    beatmapsToScores.add(new Pair<>(beatmaps.get(i), score));
                }
            }

            // TODO: REMOVAL LOG
            LOGGER.info("bestScores size: {}", beatmapsToScores.size());

            // 按 PP 从高到低排序
            beatmapsToScores.sort(Comparator
                    .comparingDouble((Pair<ObjectData /*Beatmap*/, ObjectData /*Score*/> value)
                            -> value.right().getDouble("pp"))
                    .reversed());

            // 计算 BP 的总 PP, 加权求和
            double pp = 0.0D, multiplier = 1.0D;
            for (Pair<ObjectData /*Beatmap*/, ObjectData /*Score*/>
                    beatmapsetsToScore : beatmapsToScores) {
                pp += multiplier * beatmapsetsToScore.right().getDouble("pp");
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
                ObjectData /*Beatmap*/ beatmap = beatmapsToScores.get(i).left();
                ObjectData /*Beatmapset*/ beatmapset = beatmap.get("beatmapset");
                builder.append("\n")
                        .append(beatmapset.getString("artist"))
                        .append(" - ")
                        .append(beatmapset.getString("title"))
                        .append(" (")
                        .append(beatmap.getString("version"))
                        .append("): ")
                        .append(beatmapsToScores.get(i).right().getDouble("pp"))
                        .append("pp");
            }

            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(builder.toString())));
        }
    }
}
