package io.github.rikkakawaii0612.mutsumi.api.util.math;

import java.util.*;

public class MathUtils {
    /**
     * 根据依赖关系对传入的集合进行拓扑排序: 若 A 依赖 B, 则 A 必然排在 B 之后.
     * 若存在循环依赖关系 (如 A 依赖 B, B 依赖 A), 则抛出异常.
     * 哦对这个是 Deepseek 写的, 我其实没时间设计算法 (
     *
     * @param values 要排序的集合
     * @param dependencies 对象的依赖关系, 是一个 对象 -> 依赖对象集 的映射
     * @return 排序结果
     * @throws IllegalArgumentException 若存在循环依赖关系
     */
    public static <T> List<T> topologicalSort(Collection<T> values,
                                              Map<T, ? extends Collection<T>> dependencies) {
        // 构建邻接表（有向图），边 B -> A 表示 B 必须在 A 之前
        Map<T, List<T>> graph = new HashMap<>();
        for (T t : values) {
            graph.put(t, new ArrayList<>());
        }

        // 入度表
        Map<T, Integer> inDegree = new HashMap<>();
        for (T t : values) {
            inDegree.put(t, 0);
        }

        // 根据依赖关系建立边 B -> A
        for (Map.Entry<T, ? extends Collection<T>> entry : dependencies.entrySet()) {
            T dependent = entry.getKey();   // A (依赖他人)
            for (T prerequisite : entry.getValue()) {   // B (被依赖)
                // 添加边 prerequisite -> dependent
                graph.get(prerequisite).add(dependent);
                inDegree.put(dependent, inDegree.get(dependent) + 1);
            }
        }

        // 队列保存入度为0的节点
        Queue<T> queue = new LinkedList<>();
        for (T t : values) {
            if (inDegree.get(t) == 0) {
                queue.offer(t);
            }
        }

        // 拓扑排序结果
        List<T> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            T current = queue.poll();
            result.add(current);

            for (T neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // 检查循环依赖
        if (result.size() != values.size()) {
            throw new IllegalArgumentException("Found circular dependencies");
        }

        return result;
    }
}
