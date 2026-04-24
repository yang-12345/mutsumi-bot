package io.github.rikkakawaii0612.mutsumi.scoreService;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.handler.MessageHandler;
import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceNotFoundException;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceReference;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceRequest;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ListObjectData;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
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
        this.osuApiService = this.getModule().getServiceLocator().getService("osu-api");
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
            /*String*/ ObjectData user = this.osuApiService.call(ServiceRequest.builder()
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

            /*List<Score>*/ ObjectData scores = this.osuApiService.call(ServiceRequest.builder()
                    .header("service", "getBestScores")
                    .header("id", id)
                    .header("mode", "mania")
                    .build());
            List<? extends ObjectData> list = ListObjectData.readAsMutable(scores);
            //list.removeIf(score -> score.get("beatmap").getInt("cs") != 7);
            list.sort(Comparator
                    .comparingDouble((ObjectData value)
                            -> value.getDouble("pp"))
                    .reversed());

            int limit = Math.min(200, list.size());
            double pp = 0.0D, multiplier = 1.0D;
            for (int i = 0; i < limit; i++) {
                pp += multiplier * list.get(i).getDouble("pp");
                multiplier *= 0.95D;
            }

            double bonusPp = 416.6667D * (1.0D - Math.exp(Math.log(0.995D) * Math.min(list.size(), 1000)));

            StringBuilder builder = new StringBuilder(" 用户 " + username
                    + " 共找到 " + list.size()
                    + "个有效成绩，总pp：" + (pp + bonusPp)
                    + "；" + pp + " + " + bonusPp);

            int limit2 = Math.min(200, list.size());
            for (int i = 0; i < limit2; i++) {
                /*Beatmapset*/ ObjectData beatmapset = list.get(i).get("beatmapset");
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
            //todo: 先批量获取谱面 过滤完7k再获取成绩
            String param = str.substring(6);
            /*String*/ ObjectData user = this.osuApiService.call(ServiceRequest.builder()
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

            /*List<BeatmapPlayCount>*/ ObjectData beatmaps = this.osuApiService.call(ServiceRequest.builder()
                    .header("service", "getAllPlayedBeatmaps")
                    .header("id", id)
                    .build());
            List<? extends ObjectData> list = ListObjectData.readAsMutable(beatmaps);

            List<Map.Entry<ObjectData /*Beatmap*/, ObjectData /*Beatmapset*/>>
                    beatmapsToBeatmapsets = new ArrayList<>();

            for (Iterator<? extends ObjectData> iterator = list.iterator(); iterator.hasNext(); ) {
                List<Map.Entry<ObjectData /*Beatmap*/, ObjectData /*Beatmapset*/>>
                        beatmapsToPost = new ArrayList<>();

                // 收集50个谱面一组进行post
                while (beatmapsToPost.size() < 50 && iterator.hasNext()) {
                    ObjectData /*BeatmapPlayCount*/ beatmapPlayCount = iterator.next();
                    ObjectData /*Beatmap*/ beatmap = beatmapPlayCount.get("beatmap");

                    String playMode = beatmap.get("playMode").asString();
                    if (!"mania".equals(playMode)) continue;

                    String status = beatmap.get("rankStatus").asString();
                    if (!"ranked".equals(status)) continue;

                    beatmapsToPost.add(new AbstractMap.SimpleEntry<>(beatmap, beatmapPlayCount.get("beatmapset")));
                }

                List<ObjectData /*Long*/> ids = beatmapsToPost.stream()
                        .map(o -> o.getKey().get("id"))
                        .toList();
                ObjectData /*List<Beatmap>*/ objectData = osuApiService.call(ServiceRequest.builder()
                        .header("service", "getBeatmaps")
                        .data("ids", ObjectData.of(ids))
                        .build());

                List<? extends ObjectData /*Beatmap*/> beatmapsExtended = ListObjectData.read(objectData);

                // 筛选7K谱面
                for (int i = 0; i < beatmapsExtended.size(); i++) {
                    ObjectData /*Beatmap*/ o = beatmapsExtended.get(i);
                    if (o.getInt("cs") == 7) {
                        beatmapsToBeatmapsets.add(new AbstractMap.SimpleEntry<>(o, beatmapsToPost.get(i).getValue()));
                    }
                }
            }
            LOGGER.info("rankedManiaBeatmaps beatmap size: {}", beatmapsToBeatmapsets.size());

            List<Map.Entry<ObjectData /*Beatmapset*/, ObjectData /*Score*/>>
                    beatmapsetsToScores = new ArrayList<>();
            for (int i = 0; i < beatmapsToBeatmapsets.size(); i++) {
                Map.Entry<ObjectData /*Beatmap*/, ObjectData /*Beatmapset*/>
                        entry = beatmapsToBeatmapsets.get(i);
                if (i % 10 == 0) {
                    LOGGER.info("Progress: {} / {}; {} 7K Beatmaps Found",
                            i, beatmapsToBeatmapsets.size(), beatmapsetsToScores.size());
                }


                ObjectData /*List<Score>*/ scores = this.osuApiService.call(ServiceRequest.builder()
                        .header("service", "getUserBeatmapScores")
                        .header("user", id)
                        .header("beatmap", entry.getKey().getString("id"))
                        .build());

                List<? extends ObjectData> listx = ListObjectData.read(scores);

                ObjectData /*Score*/ score = ObjectData.EMPTY;
                for (ObjectData scorex : listx) {
                    if (score.isEmpty()
                            || score.getDouble("pp") < scorex.getDouble("pp")) {
                        score = scorex;
                    }
                }

                if (!score.isEmpty()) {
                    beatmapsetsToScores.add(new AbstractMap.SimpleEntry<>(entry.getValue(), score));
                }
            }

            LOGGER.info("bestScores size: {}", beatmapsetsToScores.size());

            beatmapsetsToScores.sort(Comparator
                    .comparingDouble((Map.Entry<ObjectData, ObjectData> value)
                            -> value.getValue().getDouble("pp"))
                    .reversed());

            double pp = 0.0D, multiplier = 1.0D;
            for (Map.Entry<ObjectData, ObjectData> beatmapsetsToScore : beatmapsetsToScores) {
                pp += multiplier * beatmapsetsToScore.getValue().getDouble("pp");
                multiplier *= 0.95D;
            }

            double bonusPp = 416.6667D * (1.0D - Math.exp(Math.log(0.995D) * Math.min(beatmapsetsToScores.size(), 1000)));

            StringBuilder builder = new StringBuilder(" 用户 " + username
                    + " 共找到 " + beatmapsetsToScores.size()
                    + "个有效7k成绩，纯7k总pp：" + (pp + bonusPp)
                    + "；" + pp + " + " + bonusPp);

            int limit2 = Math.min(200, beatmapsetsToScores.size());
            for (int i = 0; i < limit2; i++) {
                /*Beatmapset*/ ObjectData beatmapset = beatmapsetsToScores.get(i).getKey();
                builder.append("\n")
                        .append(beatmapset.getString("artist"))
                        .append(" - ")
                        .append(beatmapset.getString("title"))
                        .append(": ")
                        .append(beatmapsetsToScores.get(i).getValue().getDouble("pp"))
                        .append("pp");
            }

            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(builder.toString())));
        }
    }
}
