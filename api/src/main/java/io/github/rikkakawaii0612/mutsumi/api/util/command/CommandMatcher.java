package io.github.rikkakawaii0612.mutsumi.api.util.command;

import io.github.rikkakawaii0612.mutsumi.api.util.Pair;

import java.util.*;

/**
 * <p>命令匹配器, 用于解析消息指令.
 *
 * <p>一个命令匹配器包含一个 {@link Node} 对象, 称为根节点.
 * 每个节点可以延伸出若干个子节点, 从而形成命令树.
 * 因此, 一个命令匹配器可以匹配多个语法的命令.
 *
 * <h2><a id="create">创建命令匹配器</a></h2>
 *
 * <p>直接使用 {@code new CommandMatcher(Node)} 来创建命令匹配器.
 *
 * <p>节点的创建应当使用 {@link NodeBuilder}. 在 {@link NodeBuilder} 中,
 * 我们已经提供了基本的命令节点, 可以调用这些静态方法来获取 {@link NodeBuilder} 实例.
 * 你也可以自行调用其 new 方法来创建自定义的命令节点.
 *
 * <p>要添加子节点, 只需要调用 {@link NodeBuilder#addNode(NodeBuilder)} 方法,
 * 这会将一个已有的 {@link NodeBuilder} 添加到其子节点列表中. 一个节点可以有多个子节点.
 * 调用 {@link NodeBuilder#complete()} 可以创建结束节点,
 * 这意味着命令分支可以在这里结束. 注意, 没有结束节点的分支不可能匹配成功.
 *
 * <p>命令树支持<b>重定向节点</b>. 通过创建 {@link NodeBuilder} 的临时变量,
 * 你可以向不同的节点添加同一个子节点, 从而实现同一节点的复用.
 *
 * <p>一个创建命令匹配器的例子如下:
 *
 * <pre>{@code
 *  // 单分支匹配器
 *  // 可以匹配: say Hello World!
 *  CommandMatcher matcherA = new CommandMatcher(NodeBuilder.literal("say").addNode(
 *          NodeBuilder.space().addNode(
 *                  NodeBuilder.stringVar("message").complete()
 *          )
 *  ).build());
 *
 *  // 多分支, 带重定向的匹配器
 *  // 可以匹配: tell Hello World!
 *  // 或者匹配: tell KashiKoiAstra Hello World!
 *  NodeBuilder node = NodeBuilder.space().addNode(
 *          NodeBuilder.stringVar("message").complete()
 *  );
 *  CommandMatcher matcherB = new CommandMatcher(NodeBuilder.literal("tell")
 *          .addNode(node)
 *          .addNode(
 *                  NodeBuilder.space().addNode(
 *                          NodeBuilder.stringVarWithoutSpace("target")
 *                                  .addNode(node)
 *                  )
 *          )
 *          .build());
 * }</pre>
 *
 * <h2><a id="resolve">命令解析</a></h2>
 *
 * <p>命令匹配器通过调用 {@link CommandMatcher#matches(String)} 方法来解析命令.
 * 解析会从根节点开始, 检查可能的命令分支, 直到找到匹配通过的分支.
 *
 * <p>节点的解析是<b>贪心的</b>: 每个节点会尝试匹配<b>尽可能多</b>的字符串部分.
 * 即使较短的部分可能不匹配, 解析时仍然会尝试匹配更长的部分.
 * 在寻找到节点能匹配通过的最长部分后, 节点会记录其结束索引,
 * 并尝试遍历其子节点, 并将字符串剩余部分传入子节点进行匹配.
 *
 * <p>每条命令分支都会按照创建时的顺序解析. <b>只要</b>发现有一条分支率先匹配成功,
 * 剩余的分支就会<b>被抛弃</b>. 通过创建结束节点的分支, 可以让一条命令省略后续参数匹配成功,
 * 这需要将结束节点安排为一个节点的最后一个子节点. 再次提醒, 没有结束节点的分支不可能匹配成功.
 *
 * <p>匹配命令后会返回一个 {@link Result} 对象. 这会记录是否有匹配成功的分支,
 * 并记录该分支上所有可记录参数的节点的解析结果. 可以通过 {@link Result#getValue(String, Class)}
 * 方法来获取这些结果.
 *
 * @see Node
 * @see NodeBuilder
 */
public class CommandMatcher {
    private final Node root;
    private final Map<String, Node> nameToNodes = new HashMap<>();

    public CommandMatcher(Node root) {
        this.root = root;
        this.registerNode(root);
    }

    private void registerNode(Node node) {
        String name = node.getName();
        if (!name.isEmpty()) {
            this.nameToNodes.put(node.getName(), node);
        }
        node.getChildren().forEach(this::registerNode);
    }

    public Result matches(String command) {
        List<Pair<Node, Integer>> list = parse(this.root, command);
        if (list.isEmpty()) {
            return new Result(null);
        }

        Map<String, Object> result = new HashMap<>();
        int i = 0;
        for (Pair<Node, Integer> pair : list) {
            Node node = pair.left();
            int index = pair.right();
            Optional<?> optional = node.get(command.substring(i, index));
            optional.ifPresent(o -> result.put(node.getName(), o));
            i = index;
        }

        return new Result(result);
    }

    // 返回各个节点的终止索引
    // 在没有结束节点的情况下, 此方法必定返回空列表
    private static List<Pair<Node, Integer>> parse(Node node, String command) {
        if ("_end".equals(node.getName())) {
            return List.of(new Pair<>(node, command.length()));
        }

        int index = -1;
        for (int i = 0; i <= command.length(); i++) {
            if (node.matches(command.substring(0, i))) {
                index = i;
            }
        }

        if (index != -1) {
            String sub = command.substring(index);
            for (Node child : node.getChildren()) {
                List<Pair<Node, Integer>> list = parse(child, sub);
                if (!list.isEmpty()) {
                    List<Pair<Node, Integer>> result = new ArrayList<>();
                    result.add(new Pair<>(node, index));
                    for (Pair<Node, Integer> pair : list) {
                        result.add(new Pair<>(pair.left(), index + pair.right()));
                    }
                    return result;
                }
            }
        }

        return List.of();
    }

    public static class Result {
        private final Map<String, Object> params;

        private Result(Map<String, Object> params) {
            this.params = params;
        }

        public boolean doesMatches() {
            return this.params != null;
        }

        @SuppressWarnings("unchecked")
        public <T> T getValue(String key, Class<T> type) {
            if (this.params == null) {
                throw new NoSuchElementException("No command is matched");
            }
            try {
                return (T) this.params.get(key);
            } catch (ClassCastException _) {
                return null;
            }
        }

        public <T> T getOrDefault(String key, Class<T> type, T defaultVar) {
            T value = this.getValue(key, type);
            return value != null ? value : defaultVar;
        }
    }
}
