package io.github.rikkakawaii0612.mutsumi.api.util;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

import java.util.HashMap;

/**
 * 这是为了防止相同 ID 的对象抛异常, 因为 {@link SimpleObjectIdResolver}
 * 的处理逻辑是使用 {@code ==} 来比较两个对象, 是个人都知道这肯定不通过.
 * 这里重写的逻辑是只取第一次出现的对象, 后续相同 ID 的对象并不会绑定 POJO.
 */
public class DuplicatableObjectIdResolver extends SimpleObjectIdResolver {
    @Override
    public void bindItem(ObjectIdGenerator.IdKey id, Object ob)
    {
        if (_items == null) {
            _items = new HashMap<>();
        } else {
            Object old = _items.get(id);
            if (old != null) {
                return;
            }
        }
        _items.put(id, ob);
    }
}
