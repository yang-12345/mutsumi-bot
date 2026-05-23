package io.github.rikkakawaii0612.mutsumi.api.util.command;

import io.github.rikkakawaii0612.mutsumi.api.util.Pair;
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
     * 匹配空字符串.
     * 这可以用作根节点的多选择.
     */
    public static NodeBuilder empty() {
        return new NodeBuilder("", String::isEmpty, _ -> Optional.empty());
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
     * @param name  参数名
     * @param range 范围
     */
    public static NodeBuilder intVar(String name, IntRange range) {
        return new NodeBuilder(name, param -> {
            if (param.contains(" ")) {
                return false;
            }
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
     * @param name  参数名
     * @param range 范围
     */
    public static NodeBuilder longVar(String name, LongRange range) {
        return new NodeBuilder(name, param -> {
            if (param.contains(" ")) {
                return false;
            }
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
     * @param name  参数名
     * @param range 范围
     */
    public static NodeBuilder doubleVar(String name, DoubleRange range) {
        return new NodeBuilder(name, param -> {
            if (param.contains(" ")) {
                return false;
            }
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
        return new NodeBuilder(name, param -> {
            if (!param.startsWith("\"")) {
                return !param.contains(" ");
            } else {
                if (param.length() == 1 || !param.endsWith("\"")) {
                    return false;
                }
                int count = 0;
                for (int i = param.length() - 1; i >= 1; i--) {
                    if (param.charAt(i) == '\\') {
                        count++;
                    } else {
                        break;
                    }
                }
                return count % 2 == 0;
            }
        }, param -> {
            if (!param.startsWith("\"")) {
                return Optional.of(param);
            } else {
                return Optional.of(unescape(param.substring(1, param.length() - 1)));
            }
        });
    }

    /**
     * 用于解析转义字符串.
     * By DeepSeek
     */
    private static String unescape(String param) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);
            if (c == '\\' && i + 1 < param.length()) {
                char next = param.charAt(i + 1);
                if (next == '\\') {          // 将 \\ 替换为 \
                    builder.append('\\');
                    i++;                     // 跳过下一个字符
                } else if (next == '"') {    // 将 \" 替换为 "
                    builder.append('"');
                    i++;
                } else {
                    // 其他情况保留反斜杠，下一个字符在下次循环正常处理
                    builder.append('\\');
                    // 不跳过下一个字符，让循环自然处理它
                }
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * 匹配任意单字符.
     *
     * @param name 参数名
     */
    public static NodeBuilder charVar(String name) {
        return new NodeBuilder(name, param -> param.length() == 1, param -> Optional.of(param.charAt(0)));
    }

    /**
     * 匹配整型数据范围, 格式 a~b.
     *
     * @param name 参数名
     */
    public static NodeBuilder intRange(String name) {
        return new NodeBuilder(name, param -> {
            if (!param.contains("~")) {
                return false;
            }
            String[] arr = param.split("~");
            if (arr.length != 2) {
                return false;
            }
            if (arr[0].contains(" ") || arr[1].contains(" ")) {
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
