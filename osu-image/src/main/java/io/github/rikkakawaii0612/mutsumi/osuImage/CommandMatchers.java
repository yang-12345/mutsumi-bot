package io.github.rikkakawaii0612.mutsumi.osuImage;

import io.github.rikkakawaii0612.mutsumi.api.util.command.CommandMatcher;
import io.github.rikkakawaii0612.mutsumi.api.util.math.DoubleRange;

import static io.github.rikkakawaii0612.mutsumi.api.util.command.NodeBuilder.*;

public class CommandMatchers {
    public static final CommandMatcher GREEK;

    static {
        GREEK = new CommandMatcher(literalIgnoreCase("greek")
                .then(space()
                        .then(stringVarWithoutSpace("greek")
                                .then(space()
                                        .then(doubleVar("chromaticAberration", DoubleRange.unbounded())
                                                .then(space()
                                                        .then(doubleVar("glitch", DoubleRange.unbounded())
                                                                .complete()))))
                                .complete()))
                .build());
    }
}
