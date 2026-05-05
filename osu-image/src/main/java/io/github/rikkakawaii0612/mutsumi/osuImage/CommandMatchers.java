package io.github.rikkakawaii0612.mutsumi.osuImage;

import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;

import static io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder.*;

public class CommandMatchers {
    public static final CommandMatcher GREEK;

    static {
        GREEK = new CommandMatcher(literalIgnoreCase("greek")
                .then(space()
                        .then(stringVarWithoutSpace("greek")
                                .complete()))
                .build());
    }
}
