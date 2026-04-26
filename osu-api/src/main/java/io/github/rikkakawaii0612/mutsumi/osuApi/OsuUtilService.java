package io.github.rikkakawaii0612.mutsumi.osuApi;

import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.*;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceRequest;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class OsuUtilService extends Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

    @Override
    public ObjectData call(ServiceRequest request) {
        String service = request.getHeader("service");
        switch (service) {
//            case "NodeBuilder.playMode" -> {
//                return nodeBuilderPlayMode();
//            }

            default -> {
                LOGGER.warn("Trying to access unsupported service: {}", service);
                return ObjectData.EMPTY;
            }
        }
    }

//    private static ObjectData nodeBuilderPlayMode(String name) {
//        return new NodeBuilder(name, param -> param.);
//    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

    @Override
    public String getId() {
        return "osu-api-util";
    }
}
