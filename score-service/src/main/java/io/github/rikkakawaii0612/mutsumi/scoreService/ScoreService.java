package io.github.rikkakawaii0612.mutsumi.scoreService;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.handler.MessageHandler;
import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceNotFoundException;
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

    public ScoreService() {
    }

    @Override
    public ObjectData call(ServiceRequest request) {
        return ObjectData.EMPTY;
    }

    @Override
    public void load() {
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

        if (str.substring(1).startsWith("bp7k ")) {
            Service osuApiService = this.getModule().getServiceLocator().getService("osu-api-base")
                    .orElseThrow(ServiceNotFoundException::new);
            String param = str.substring(6);
            /*String*/ ObjectData user = osuApiService.call(ServiceRequest.builder()
                    .header("service", "getUser")
                    .header("username", param)
                    .build());

            if (user.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(Message.text(" 没有找到用户名或ID为 " + param + " 的用户。")));
                return;
            }

            Service osuDataService = this.getModule().getServiceLocator().getService("osu-api-util")
                    .orElseThrow(ServiceNotFoundException::new);
            String username = osuDataService.call(user, "name").asString();
            String id = osuDataService.call(user, "id").asString();

            bot.sendMessage(group.getId(), Message.at(sender.getId())
                    .append(Message.text(" 正在查找用户 " + username + "的7k信息……" +
                            "\n这可能需要一些时间。")));

            /*List<BeatmapPlayCount>*/ ObjectData beatmaps = osuApiService.call(ServiceRequest.builder()
                    .header("service", "getAllPlayedBeatmaps")
                    .header("id", id)
                    .build());
            List<? extends ObjectData> list = ListObjectData.read(beatmaps);
            List<Map.Entry<ObjectData, ObjectData>> rankedManiaBeatmaps = new ArrayList<>();
            for (ObjectData beatmapPlayCount : list) {
                /*Beatmap*/ ObjectData beatmap = osuDataService.call(beatmapPlayCount, "beatmap");

                String playMode = osuDataService.call(beatmap, "playMode").asString();
                if (!"mania".equals(playMode)) continue;

                String status = osuDataService.call(beatmap, "rankStatus").asString();
                if (!"ranked".equals(status)) continue;

                /*Beatmapset*/ ObjectData beatmapset = osuDataService.call(beatmapPlayCount, "beatmapset");
                if (beatmapset.isEmpty()) {
                    continue;
                }
                rankedManiaBeatmaps.add(new AbstractMap.SimpleEntry<>(beatmap, beatmapset));
            }
            LOGGER.info("rankedManiaBeatmaps beatmap size: {}", rankedManiaBeatmaps.size());

            List<Map.Entry<ObjectData, ObjectData>> beatmapsetsToScores = new ArrayList<>();
            int k = 0;
            for (Map.Entry<ObjectData, ObjectData> entry : rankedManiaBeatmaps) {
                /*BeatmapUserScore*/ ObjectData beatmapUserScore = osuApiService.call(ServiceRequest.builder()
                        .header("service", "getUserBeatmapScore")
                        .header("user", id)
                        .header("beatmap", osuDataService.call(entry.getKey(), "id").asString())
                        .build());

                /*Score*/ ObjectData score = osuDataService.call("score", beatmapUserScore);
                if (!score.isEmpty()) {
                    /*Beatmap*/ ObjectData beatmap = osuDataService.call(score, "beatmap");
                    int keyCount = osuDataService.call(beatmap, "cs").asInt();
                    if (keyCount == 7) {
                        beatmapsetsToScores.add(new AbstractMap.SimpleEntry<>(entry.getValue(), score));
                    }
                }
                k++;
                if (k % 10 == 0) {
                    LOGGER.info("Progress: {} / {}; {} 7K Beatmaps Found", k, rankedManiaBeatmaps.size(), beatmapsetsToScores.size());
                }
            }
            LOGGER.info("beatmapsetsToScores size: {}", beatmapsetsToScores.size());

            beatmapsetsToScores.sort(Comparator
                    .comparingDouble((Map.Entry<ObjectData, ObjectData> value)
                            -> osuDataService.call(value.getValue(), "pp").asDouble())
                    .reversed());
            int limit = Math.min(200, beatmapsetsToScores.size());
            double pp = 0.0D, multiplier = 1.0D;
            for (int i = 0; i < limit; i++) {
                pp += multiplier * osuDataService.call(beatmapsetsToScores.get(i).getValue(), "pp").asDouble();
                multiplier *= 0.95D;
            }

            pp += 416.6667D * (1.0D - Math.exp(Math.log(0.9994D) * Math.min(beatmapsetsToScores.size(), 1000)));

            StringBuilder builder = new StringBuilder(" 用户 " + username
                    + " 共找到 " + beatmapsetsToScores.size()
                    + "个有效7k成绩，纯7k总pp：" + pp);

            int limit2 = Math.min(20, beatmapsetsToScores.size());
            for (int i = 0; i < limit2; i++) {
                /*Beatmapset*/ ObjectData beatmapset = beatmapsetsToScores.get(i).getKey();
                builder.append("\n")
                        .append(osuDataService.call(beatmapset, "artist").asString())
                        .append(" - ")
                        .append(osuDataService.call(beatmapset, "title").asString())
                        .append(": ")
                        .append(osuDataService.call(beatmapsetsToScores.get(i).getValue(), "pp").asDouble())
                        .append("pp");
            }

            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(builder.toString())));
        }
    }
}
