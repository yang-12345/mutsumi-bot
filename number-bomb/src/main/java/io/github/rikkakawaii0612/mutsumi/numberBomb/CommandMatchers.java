package io.github.rikkakawaii0612.mutsumi.numberBomb;

import io.github.rikkakawaii0612.mutsumi.api.util.Pair;
import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder;
import io.github.rikkakawaii0612.mutsumi.api.util.math.IntRange;

import java.util.Optional;

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

    public static NodeBuilder intRange(String name) {
        return new NodeBuilder(name, param -> {
            if (!param.contains("~")) {
                return false;
            }
            String[] arr = param.split("~");
            if (arr.length != 2) {
                return false;
            }
            try {
                Integer.parseInt(arr[0]);
                Integer.parseInt(arr[1]);
                return true;
            } catch (NumberFormatException _) {
                return false;
            }
        }, param -> {
            String[] arr = param.split("~");
            if (arr.length != 2) {
                return Optional.empty();
            }
            try {
                int min = Integer.parseInt(arr[0]);
                int max = Integer.parseInt(arr[1]);
                return Optional.of(new Pair<>(min, max));
            } catch (NumberFormatException _) {
                return Optional.empty();
            }
        });
    }
}
