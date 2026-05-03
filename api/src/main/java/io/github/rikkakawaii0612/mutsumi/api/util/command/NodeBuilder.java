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

    public NodeBuilder then(NodeBuilder nodeBuilder) {
        this.children.add(nodeBuilder);
        return this;
    }

    public Node build() {
        return new Node(this.name, this.matcher, this.getter,
                this.children.stream().map(NodeBuilder::build).toList());
    }

    /**
     * 添加一个结束节点.
     */
    public NodeBuilder complete() {
        return this.then(new NodeBuilder("_end", String::isBlank, _ -> Optional.empty()));
    }

    /**
     * 严格匹配给定文本, 区分大小写.
     *
     * @param arg 给定文本
     */
    public static NodeBuilder literal(String arg) {
        return new NodeBuilder("", arg::equals, _ -> Optional.empty());
    }

    /**
     * 匹配给定文本, 不区分大小写.
     *
     * @param arg 给定文本
     */
    public static NodeBuilder literalIgnoreCase(String arg) {
        return new NodeBuilder("", arg::equalsIgnoreCase, _ -> Optional.empty());
    }

    /**
     * 匹配一个或一个以上的空格. 由于贪心算法, 这将匹配足够多的空格.
     * 常用于分隔参数.
     */
    public static NodeBuilder space() {
        return new NodeBuilder("", param -> !param.isEmpty() && param.isBlank(), _ -> Optional.empty());
    }

    /**
     * 匹配空字符串或者任意多个空格. 由于贪心算法, 这将匹配足够多的空格.
     * 常用于分隔允许不用空格分隔的参数.
     */
    public static NodeBuilder spaceOrEmpty() {
        return new NodeBuilder("", String::isBlank, _ -> Optional.empty());
    }

    /**
     * 匹配给定范围内的整型数据.
     *
     * @param name 参数名
     * @param range 范围
     */
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

    /**
     * 匹配给定范围内的长整型数据.
     *
     * @param name 参数名
     * @param range 范围
     */
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

    /**
     * 匹配给定范围内的双精度浮点型数据.
     *
     * @param name 参数名
     * @param range 范围
     */
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

    /**
     * 匹配任意字符串. 注意, 由于贪心算法, 从该节点开始的所有字符都会被匹配.
     *
     * @param name 参数名
     */
    public static NodeBuilder stringVar(String name) {
        return new NodeBuilder(name, _ -> true, Optional::of);
    }

    /**
     * 匹配任意不带空格的字符串. 由于贪心算法, 这将匹配尽可能长的字符串.
     *
     * @param name 参数名
     */
    public static NodeBuilder stringVarWithoutSpace(String name) {
        return new NodeBuilder(name, param -> !param.contains(" "), Optional::of);
    }

    /**
     * 匹配任意单字符.
     *
     * @param name 参数名
     */
    public static NodeBuilder charVar(String name) {
        return new NodeBuilder(name, param -> param.length() == 1, param -> Optional.of(param.charAt(0)));
    }
}
