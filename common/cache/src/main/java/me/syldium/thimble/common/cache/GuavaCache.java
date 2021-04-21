package me.syldium.thimble.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

class GuavaCache<K, V> implements CacheService<K, V> {

    private final Cache<K, V> cache;

    GuavaCache(long duration, @NotNull TimeUnit unit) {
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(duration, unit).build();
    }

    @Override
    public @NotNull V get(@NotNull K key, @NotNull Function<@NotNull K, @NotNull V> loader) {
        V value = this.cache.getIfPresent(key);
        if (value != null) {
            return value;
        }

        value = loader.apply(key);
        this.cache.put(key, value);
        return value;
    }

    @Override
    public void put(@NotNull K key, @NotNull V value) {
        this.cache.put(key, value);
    }

    @Override
    public void invalidate(@NotNull K key) {
        this.cache.invalidate(key);
    }
}
