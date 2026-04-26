package io.github.rikkakawaii0612.mutsumi.api.util.command;

import io.github.rikkakawaii0612.mutsumi.api.util.math.DoubleRange;
import io.github.rikkakawaii0612.mutsumi.api.util.math.IntRange;
import io.github.rikkakawaii0612.mutsumi.api.util.math.LongRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class NodeBuilder {
    private final String name;
    private final Function<String, Boolean> matcher;
    private final Function<String, Optional<?>> getter;
    private final List<NodeBuilder> children;

    public NodeBuilder(String name,
                 Function<String, Boolean> matcher,
                 Function<String, Optional<?>> getter) {
        this.name = name;
        this.matcher = matcher;
        this.getter = getter;
        this.children = new ArrayList<>();
    }

    public NodeBuilder addNode(NodeBuilder nodeBuilder) {
        this.children.add(nodeBuilder);
        return this;
    }

    public Node build() {
        return new Node(this.name, this.matcher, this.getter,
                this.children.stream().map(NodeBuilder::build).toList());
    }

    public NodeBuilder complete() {
        return this.addNode(new NodeBuilder("_end", String::isBlank, _ -> Optional.empty()));
    }

    public static NodeBuilder literal(String arg) {
        return new NodeBuilder("", arg::equals, _ -> Optional.empty());
    }

    public static NodeBuilder literalIgnoreCase(String arg) {
        return new NodeBuilder("", arg::equalsIgnoreCase, _ -> Optional.empty());
    }

    public static NodeBuilder space() {
        return new NodeBuilder("", param -> !param.isEmpty() && param.isBlank(), _ -> Optional.empty());
    }

    public static NodeBuilder intVar(String name, IntRange range) {
        return new NodeBuilder(name, param -> {
            try {
                return range.matches(Integer.parseInt(param));
            } catch (NumberFormatException _) {
                return false;
            }
        }, param -> {
            try {
                return Optional.of(Integer.parseInt(param));
            } catch (NumberFormatException _) {
                return Optional.empty();
            }
        });
    }

    public static NodeBuilder longVar(String name, LongRange range) {
        return new NodeBuilder(name, param -> {
            try {
                return range.matches(Long.parseLong(param));
            } catch (NumberFormatException _) {
                return false;
            }
        }, param -> {
            try {
                return Optional.of(Long.parseLong(param));
            } catch (NumberFormatException _) {
                return Optional.empty();
            }
        });
    }

    public static NodeBuilder doubleVar(String name, DoubleRange range) {
        return new NodeBuilder(name, param -> {
            try {
                return range.matches(Double.parseDouble(param));
            } catch (NumberFormatException _) {
                return false;
            }
        }, param -> {
            try {
                return Optional.of(Double.parseDouble(param));
            } catch (NumberFormatException _) {
                return Optional.empty();
            }
        });
    }

    public static NodeBuilder stringVar(String name) {
        return new NodeBuilder(name, _ -> true, Optional::of);
    }

    public static NodeBuilder stringVarWithoutSpace(String name) {
        return new NodeBuilder(name, param -> !param.contains(" "), Optional::of);
    }
}
