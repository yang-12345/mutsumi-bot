package io.github.rikkakawaii0612.mutsumi.scoreService;

import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder;
import io.github.rikkakawaii0612.mutsumi.osuApi.util.OsuCommandNodes;

import static io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder.*;

public class CommandMatchers {
    public static final CommandMatcher BP;

    static {
        NodeBuilder node = space().then(
                stringVarWithoutSpace("user").complete()
        );
        BP = new CommandMatcher(literalIgnoreCase("bp")
                .then(OsuCommandNodes.nodeBuilderPlayMode("playMode")
                        .then(node))
                .then(node)
                .build());
    }

    public static final CommandMatcher BP7K = new CommandMatcher(literalIgnoreCase("bp7k").then(
            space().then(
                    stringVarWithoutSpace("user").complete()
            )
    ).build());
}
