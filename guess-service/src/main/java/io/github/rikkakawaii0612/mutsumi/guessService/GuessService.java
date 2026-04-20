package io.github.rikkakawaii0612.mutsumi.guessService;

import io.github.rikkakawaii0612.mutsumi.api.contact.Group;
import io.github.rikkakawaii0612.mutsumi.api.contact.Member;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.contact.message.Message;
import io.github.rikkakawaii0612.mutsumi.api.handler.MessageHandler;
import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceNotFoundException;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceRequest;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Extension
public class GuessService extends Service implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("GuessService");

    private final Map<Long, GameInfo> gameInfos = new ConcurrentHashMap<>();

    public GuessService() {
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

        if (str.substring(1).startsWith("info ")) {
            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(" 处理中……")));

            Service osuApiService = this.getModule().getServiceLocator().getService("osu-api-base")
                    .orElseThrow(ServiceNotFoundException::new);
            ObjectData user = osuApiService.call(ServiceRequest.builder()
                    .header("service", "getUser")
                    .header("username", str.substring(6))
                    .build());

            Service osuDataService = this.getModule().getServiceLocator().getService("osu-api-util")
                    .orElseThrow(ServiceNotFoundException::new);
            String username = osuDataService.call("name", user).asString();
            String id = osuDataService.call("id", user).asString();
            bot.sendMessage(group.getId(), Message.at(sender.getId()).append(Message.text(" id: " + id + ", username: " + username)));
        }
    }
}
