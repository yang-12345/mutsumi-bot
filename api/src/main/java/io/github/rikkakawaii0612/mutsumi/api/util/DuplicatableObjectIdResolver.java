package io.github.rikkakawaii0612.mutsumi.api.util;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

import java.util.HashMap;

/**
 * <p>这个类是为了防止相同 ID 的对象抛异常, 因为 {@link SimpleObjectIdResolver}
 * 的处理逻辑是使用 {@code ==} 来比较两个对象. 这到底谁写的逻辑,
 * 是个人都知道反序列化的时候这肯定不通过, 等于没写.
 *
 * <p>这里重写的逻辑是只取第一次出现的对象, 后续相同 ID 的对象并不会绑定 POJO.
 *
 * <p>使用时, 在注解 {@link JsonIdentityInfo} 里指定 {@code resolver}
 * 为这个类即可.
 */
public class DuplicatableObjectIdResolver extends SimpleObjectIdResolver {
    @Override
    public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {
        if (_items == null) {
            _items = new HashMap<>();
        } else {
            Object old = _items.get(id);
            if (old != null) {
                // 与 SimpleObjectIdResolver 的差异: 重复 ID 不抛异常, 而是跳过
                return;
            }
        }
        _items.put(id, ob);
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return new DuplicatableObjectIdResolver();
    }
}
