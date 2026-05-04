package io.github.rikkakawaii0612.mutsumi.numberBomb;

import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.api.util.math.IntRange;

import static io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder.*;

public class CommandMatchers {
    public static final CommandMatcher BOMB_START;
    public static final CommandMatcher BOMB_SELECT;
    public static final CommandMatcher BOMB_ANSWER;

    static {
        BOMB_START = new CommandMatcher(literalIgnoreCase("bomb")
                .then(spaceOrEmpty().then(
                        intRange("range")
                                .complete()))
                .build());

        BOMB_SELECT = new CommandMatcher(literalIgnoreCase("bomb")
                .then(spaceOrEmpty().then(
                        intVar("value", IntRange.unbounded())
                                .complete()))
                .build());

        BOMB_ANSWER = new CommandMatcher(literalIgnoreCase("bomb")
                .then(space()
                        .then(literalIgnoreCase("ans")
                                .complete())
                        .then(literalIgnoreCase("answer")
                                .complete()))
                .build());
    }
}
