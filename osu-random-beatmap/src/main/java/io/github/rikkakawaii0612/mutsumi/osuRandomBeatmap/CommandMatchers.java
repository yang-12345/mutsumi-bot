package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.api.util.math.DoubleRange;

import static io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder.*;

public class CommandMatchers {
    public static final CommandMatcher SELECT_TYPE;
    public static final CommandMatcher SET_PARAM;
    public static final CommandMatcher FINISH;
    public static final CommandMatcher CANCEL;

    static {
        SELECT_TYPE = new CommandMatcher(literalIgnoreCase("randBeatmap")
                .then(space()
                        .then(stringVarWithoutSpace("type")
                                .complete()))
                .build());
        SET_PARAM = new CommandMatcher(literalIgnoreCase("randBeatmap")
                .then(space()
                        .then(literalIgnoreCase("set")
                                .then(space()
                                        .then(stringVarWithoutSpace("key")
                                                .then(space()
                                                        .then(stringVarWithoutSpace("value")
                                                                .complete()))))))
                .build());
        FINISH = new CommandMatcher(literalIgnoreCase("randBeatmap")
                .then(space()
                        .then(literalIgnoreCase("finish")
                                .complete())
                        .then(literalIgnoreCase("ok")
                                .complete()))
                .build());
        CANCEL = new CommandMatcher(literalIgnoreCase("randBeatmap")
                .then(space()
                        .then(literalIgnoreCase("cancel")
                                .complete())
                        .then(literalIgnoreCase("clear")
                                .complete()))
                .build());
    }
}
