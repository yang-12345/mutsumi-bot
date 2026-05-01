package io.github.rikkakawaii0612.mutsumi.api;

import io.github.rikkakawaii0612.mutsumi.api.contact.BotBus;

/**
 * 这是什么?
 *
 * <h1>若 叶 睦 ? !</h1>
 *
 * <p></p><i><s>不是哥们, 谁起的名字啊.</s></i>
 *
 * <p>我起的嘿嘿 (
 *
 * <h2>Mutsumi 类的使用</h2>
 *
 * <p>{@link Mutsumi} 类只会在主程序启动时创建一份单例.
 * 其包含当前程序运行的 Bot 总线, 服务加载器, 配置等数据与信息.
 * 如果你写过 Minecraft Mod 的话, 这东西和 {@code MinecraftClient}
 * 或者 {@code Minecraft} 的位置差不多.
 * {@link Mutsumi} 接口的唯一实现类是 {@code MutsumiImpl} (位于 app 模块).
 *
 * <p>在服务被加载时, {@link Service#load(String, ServiceLookup)}
 * 方法中传入的 {@link ServiceLookup} 对象内就包含 {@link Mutsumi} 对象.
 * 通常来说, 这是服务获取 {@link Mutsumi} 对象的唯一方法.
 *
 * <h2>配置</h2>
 *
 * <p>可以从 {@link Mutsumi} 实例中获取 {@link Config} 实例.
 * {@link Mutsumi} 会从 configs 目录加载配置.
 *
 * <p>配置以 JSON 格式存储. 每个服务可以自由读取配置, 同时 {@link Mutsumi}
 * 对象也会读取 configs/mutsumi.json 来获取配置.
 *
 * <p>配置在重新加载服务时会重新读取. 同时, 你也可以单独重新加载配置.
 *
 * <h2>自定义名称</h2>
 *
 * <p><s>总有人不喜欢管这东西叫 Mutsumi, 而想叫 Arisu. {@code @6r3²n}</s>
 *
 * <p>可以通过设置自定义名称来改变 Mutsumi 的自称. 发送消息时,
 * 一切 Mutsumi 的自称都应该改成 {@link #getName()} 方法引用.
 */
public interface Mutsumi {
    BotBus getBotBus();
}
