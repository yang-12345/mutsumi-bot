package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 到达容量限制后, 自动删除存取频率最低的键值对的 Map.
 */
public class LimitedMap<K, V> extends ConcurrentHashMap<K, V> {
    private final ConcurrentHashMap<K, Integer> frequencies = new ConcurrentHashMap<>();
    private final int capacity;
    private final Object lock = new Object();

    public LimitedMap(int capacity) {
        this.capacity = capacity;
    }

    public Optional<V> cache(K key, Supplier<Optional<V>> ifAbsent) {
        if (!this.frequencies.containsKey(key)) {
            this.frequencies.put(key, 1);
        } else {
            this.frequencies.put(key, this.frequencies.get(key) + 1);
        }

        synchronized (this.lock) {
            if (this.size() >= this.capacity) {
                Optional<Entry<K, Integer>> optional = this.frequencies.entrySet().stream()
                        .min(Comparator.comparingInt(Entry::getValue));
                optional.ifPresent(entry -> this.remove(entry.getKey()));
            }
        }

        if (this.containsKey(key)) {
            return Optional.of(this.get(key));
        }

        Optional<V> optional = ifAbsent.get();
        if (optional.isPresent()) {
            V value = optional.get();
            this.put(key, value);
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }
}
