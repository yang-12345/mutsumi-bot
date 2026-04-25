package io.github.rikkakawaii0612.mutsumi.api.util;

/**
 * 数据对, 包含两个对象, 允许不同类型.
 *
 * @param left 第一个对象
 * @param right 第二个对象
 * @param <A> 第一个对象的类型
 * @param <B> 第二个对象的类型
 */
public record Pair<A, B>(A left, B right) {
}
