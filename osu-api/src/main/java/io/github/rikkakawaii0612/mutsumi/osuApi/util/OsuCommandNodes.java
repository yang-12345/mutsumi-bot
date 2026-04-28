package io.github.rikkakawaii0612.mutsumi.osuApi.util;

import io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.PlayMode;

import java.util.Optional;

public class OsuCommandNodes {
    public static NodeBuilder nodeBuilderPlayMode(String name) {
        return new NodeBuilder(name, param -> PlayMode.of(param) != null,
                param -> Optional.ofNullable(PlayMode.of(param)));
    }
}
