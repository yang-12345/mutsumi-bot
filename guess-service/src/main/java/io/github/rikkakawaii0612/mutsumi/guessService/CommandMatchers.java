package io.github.rikkakawaii0612.mutsumi.guessService;

import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder;
import io.github.rikkakawaii0612.mutsumi.osuApi.util.OsuCommandNodes;

import static io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder.*;

public class CommandMatchers {
    public static final CommandMatcher GUESS;

    static {
        NodeBuilder node = space().then(
                stringVarWithoutSpace("user").complete()
        );
        GUESS = new CommandMatcher(literalIgnoreCase("guess")
                .then(spaceOrEmpty()
                        .then(OsuCommandNodes.nodeBuilderPlayMode("playMode")
                                .then(node))
                        .then(node)
                        .complete())
                .build());
    }
}
