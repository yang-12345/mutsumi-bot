package io.github.rikkakawaii0612.mutsumi.guessService;

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
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Extension
public class GuessService extends Service implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("GuessService");
    private ServiceReference osuApiService;

    private final Map<Long, GameInfo> gameInfos = new ConcurrentHashMap<>();

    public GuessService() {
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
            ObjectData/*User*/ user = this.osuApiService.call(ServiceRequest.builder()
                    .header("service", "getUser")
                    .header("username", userParam)
                    .build());
            if (user.isEmpty()) {
                bot.sendMessage(group.getId(), Message.at(sender.getId()).append(" 找不到用户。你是不是输错了……？"));
                return;
            }

            List<? extends ObjectData/*Score*/> bestScores = ListObjectData.readAsMutable(
                    this.osuApiService.call(ServiceRequest.builder()
                        .header("service", "getBestScores")
                        .header("id", user.getString("id"))
                        .build()
                    ));
            Random random = new Random();
            List<ObjectData/*Beatmap*/> beatmaps = new ArrayList<>();
            for (int i = 0; i < 10 && !bestScores.isEmpty();) {
                ObjectData/*Score*/ score = bestScores.remove(random.nextInt(bestScores.size()));
                ObjectData/*Beatmap*/ beatmap = score.get("beatmap");
                boolean foundSimilar = false;
                for (ObjectData/*Beatmap*/ o : beatmaps) {
                    if (beatmap.getString("title").equals(o.getString("title"))) {
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

            GameInfo gameInfo = new GameInfo(user, beatmaps, "mania", true, false);
            this.gameInfos.put(group.getId(), gameInfo);
            bot.sendMessage(group.getId(), Message.at(sender.getId())
                    .append(" 从用户 " + user.getString("name")
                            + " 选取了 " + beatmaps.size() + " 个成绩。开始猜歌！"));

        }
    }
}
