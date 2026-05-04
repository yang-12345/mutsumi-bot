package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.api.util.math.DoubleRange;

import static io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder.*;

public class CommandMatchers {
    public static final CommandMatcher JACK;
    public static final CommandMatcher SPEED;

    static {
        JACK = new CommandMatcher(literalIgnoreCase("randBeatmap")
                .then(space()
                        .then(literalIgnoreCase("jack")
                                .then(space()
                                        .then(doubleVar("bpm", DoubleRange.unbounded())
                                                .then(space()
                                                        .then(intRange("time")
                                                                .then(space()
                                                                        .then(stringVarWithoutSpace("config")
                                                                                .complete()))))))))
                .build());
        SPEED = new CommandMatcher(literalIgnoreCase("randBeatmap")
                .then(space()
                        .then(literalIgnoreCase("speed")
                                .then(space()
                                        .then(doubleVar("bpm", DoubleRange.unbounded())
                                                .then(space()
                                                        .then(intRange("time")
                                                                .then(space()
                                                                        .then(doubleVar("bumpChance", DoubleRange.unbounded())
                                                                                .then(space()
                                                                                        .then(stringVarWithoutSpace("config")
                                                                                                .complete()))))))))))

                .build());
    }
}
