package me.syldium.thimble.common.cache;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

class DummyCache<K, V> implements CacheService<K, V> {

    DummyCache(long duration, TimeUnit unit) {
    }

    DummyCache() {
    }

    @Override
    public @NotNull V get(@NotNull K key, @NotNull Function<@NotNull K, @NotNull V> loader) {
        return loader.apply(key);
    }

    @Override
    public void put(@NotNull K key, @NotNull V value) {

    }

    @Override
    public void invalidate(@NotNull K key) {

    }
}
