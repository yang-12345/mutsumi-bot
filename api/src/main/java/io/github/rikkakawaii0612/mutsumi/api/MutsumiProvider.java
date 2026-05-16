package io.github.rikkakawaii0612.mutsumi.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * 用于获取 Mutsumi 单例. 在主程序开始运行后, 其应该能返回 Mutsumi 单例.
 * 若调用时不能确保主程序已初始化, 则可能获得 null.
 */
public class MutsumiProvider {

    /**
     * Mutsumi 单例.
     */
    private static Mutsumi instance;

    /**
     * 获取 Mutsumi 单例.
     *
     * @return Mutsumi 单例. 若主程序未初始化完毕, 则可能返回 null
     */
    public static Mutsumi getInstance() {
        return instance;
    }

    /**
     * <b>该方法不应该被其它任何模块调用!</b>
     *
     * <p>此方法用于设置 {@link #instance} 字段, 只应当在主模块 (app) 调用.
     */
    @ApiStatus.Internal
    public static void set(Mutsumi mutsumi) {
        if (instance != null) {
            throw new IllegalStateException("Mutsumi is already set");
        }
        instance = mutsumi;
    }
}
