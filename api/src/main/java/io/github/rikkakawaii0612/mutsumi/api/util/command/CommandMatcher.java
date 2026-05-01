package io.github.rikkakawaii0612.mutsumi.api.util.command;

import io.github.rikkakawaii0612.mutsumi.api.util.Pair;

import java.util.*;

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
