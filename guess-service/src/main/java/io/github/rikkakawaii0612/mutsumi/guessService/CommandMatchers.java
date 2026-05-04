package io.github.rikkakawaii0612.mutsumi.guessService;

import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder;
import io.github.rikkakawaii0612.mutsumi.api.util.math.IntRange;
import io.github.rikkakawaii0612.mutsumi.osuApi.util.OsuCommandNodes;

import static io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder.*;

public class CommandMatchers {
    public static final CommandMatcher LETTER;
    public static final CommandMatcher OPEN;
    public static final CommandMatcher GUESS;
    public static final CommandMatcher ANSWER;

    static {
        NodeBuilder node1 = space()
                .then(stringVarWithoutSpace("user")
                        .then(space()
                                .then(intVar("count", IntRange.unbounded())
                                        .complete())
                        )
                        .complete());
        LETTER = new CommandMatcher(literalIgnoreCase("letter")
                .then(OsuCommandNodes.nodeBuilderPlayMode("playMode")
                        .then(node1))
                .then(node1)
                .build());

        OPEN = new CommandMatcher(literalIgnoreCase("open")
                .then(spaceOrEmpty()
                        .then(charVar("char")
                                .complete()))
                .build());

        NodeBuilder node2 = spaceOrEmpty()
                .then(intVar("index", IntRange.unbounded())
                        .then(space()
                                .then(stringVar("song")
                                        .complete())));
        GUESS = new CommandMatcher(empty()
                .then(literalIgnoreCase("guess")
                        .then(node2))
                .then(literalIgnoreCase("open")
                        .then(node2))
                .build());

        NodeBuilder node3 = spaceOrEmpty()
                .then(intVar("index", IntRange.unbounded())
                        .complete());
        ANSWER = new CommandMatcher(empty()
                .then(literalIgnoreCase("answer")
                        .then(node3)
                        .complete())
                .then(literalIgnoreCase("ans")
                        .then(node3)
                        .complete())
                .build());
    }
}
