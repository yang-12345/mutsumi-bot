package io.github.rikkakawaii0612.mutsumi.api.util.math;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {
    /**
     * 根据依赖关系对传入的集合进行拓扑排序: 若 A 依赖 B, 则 A 必然排在 B 之后.
     * 若存在循环依赖关系 (如 A 依赖 B, B 依赖 A), 则抛出异常.
     * 哦对这个是 Deepseek 写的, 我其实没时间设计算法 (
     *
     * @param values       要排序的集合
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

    /**
     * 根据权重列表，从元素列表中随机选择一个元素
     *
     * @param items   元素列表
     * @param weights 对应的权重列表 (非负，权重总和必须 > 0)
     * @param <T>     元素类型
     * @return 被选中的元素
     * @throws IllegalArgumentException 如果参数非法 (null、长度不一致、包含负数或无穷大、总权重非正)
     */
    public static <T> T choose(List<T> items, List<Double> weights) {
        // 参数校验
        if (items == null || weights == null) {
            throw new NullPointerException("null");
        }
        if (items.size() != weights.size()) {
            throw new IllegalArgumentException("Inconsistent length of items and weights");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Empty items");
        }

        // 计算总权重，同时检查权重的有效性
        double totalWeight = 0.0;
        for (double w : weights) {
            if (w < 0) {
                throw new IllegalArgumentException("Weight must be not negative: " + w);
            }
            if (Double.isNaN(w) || Double.isInfinite(w)) {
                throw new IllegalArgumentException("Weight must be finite: " + w);
            }
            totalWeight += w;
        }
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Total weight must be positive");
        }

        // 生成 [0, totalWeight) 范围内的随机数
        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;

        // 累加权重并找到随机数落在的区间
        double cumulative = 0.0;
        for (int i = 0; i < items.size(); i++) {
            cumulative += weights.get(i);
            if (random < cumulative) {
                return items.get(i);
            }
        }

        // 理论上应已在上面的循环中返回，此处作为浮点误差的保底（返回最后一个元素）
        return items.getLast();
    }
}
