package io.github.rikkakawaii0612.mutsumi.guessService;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.osuApi.OsuApiService;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.Beatmap;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.PlayMode;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.Score;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuessService implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("GuessService");
    private OsuApiService osuApiService;

    private final Map<Long, GameInfo> gameInfos = new ConcurrentHashMap<>();

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

//        if (str.substring(1).equals("ping")) {
//            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(" Mutsumi is here.")));
//        }
//
//        if (str.substring(1).startsWith("info ")) {
//            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(" 处理中……")));
//
//            Service osuApiService = this.getModule().getServiceLocator().getService("osu-api")
//                    .orElseThrow(ServiceNotFoundException::new);
//            ObjectData user = osuApiService.call(ServiceRequest.builder()
//                    .header("service", "getUser")
//                    .header("username", str.substring(6))
//                    .build());
//
//            String username = user.getString("name");
//            String id = user.getString("id");
//            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(" id: " + id + ", username: " + username)));
//        }

        String command = str.substring(1);
        if (command.trim().startsWith("guess ")) {
            String[] params = command.substring(6).trim().split(" +");
            if (params.length == 0) {
                bot.sendMessage(group.getId(), Message.at(sender.getId()).append(" 请输入文本。"));
                return;
            }

            String userParam = params[0];
            Optional<User> optional = this.osuApiService.getUser(userParam);
            if (optional.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId()).append(" 找不到用户。你是不是输错了……？"));
                return;
            }

            User user = optional.get();
            Optional<List<Score>> optional2 = this.osuApiService.getBestScores(user.id, PlayMode.MANIA);
            if (optional2.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(" 获取用户 " + user.id + " 最好成绩时发生错误。" +
                                "\n……是不是应该报告给开发者？"));
                return;
            }
            List<Score> bestScores = optional2.get();

            Random random = new Random();
            List<Beatmap> beatmaps = new ArrayList<>();
            for (int i = 0; i < 10 && !bestScores.isEmpty();) {
                Score score = bestScores.remove(random.nextInt(bestScores.size()));
                Beatmap beatmap = score.beatmap;
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
                bot.sendMessage(group.getId(), Message.at(sender.getId())
                        .append(" 找不到用户 " + user.id + " 的最好成绩。"));
                return;
            }

            GameInfo gameInfo = new GameInfo(user, beatmaps, "mania", true, false);
            this.gameInfos.put(group.getId(), gameInfo);
            bot.sendMessage(group.getId(), Message.at(sender.getId())
                    .append(" 从用户 " + user.username
                            + " 选取了 " + beatmaps.size() + " 个成绩。开始猜歌！"));

        }
    }
}
