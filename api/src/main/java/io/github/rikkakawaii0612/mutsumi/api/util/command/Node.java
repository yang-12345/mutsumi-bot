package io.github.rikkakawaii0612.mutsumi.api.util.command;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class Node {
    private final String name;
    private final Function<String, Boolean> matcher;
    private final Function<String, Optional<?>> getter;
    private final List<Node> children;

    Node(String name,
         Function<String, Boolean> matcher,
         Function<String, Optional<?>> getter,
         List<Node> children) {
        this.name = name;
        this.matcher = matcher;
        this.getter = getter;
        this.children = children;
    }

    boolean matches(String command) {
        return this.matcher.apply(command);
    }

    Optional<?> get(String command) {
        return this.getter.apply(command);
    }

    public String getName() {
        return this.name;
    }

    public List<Node> getChildren() {
        return this.children;
    }
}
