package me.syldium.thimble.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

class CaffeineCache<K, V> implements CacheService<K, V> {

    private final Cache<@NotNull K, @NotNull V> cache;

    CaffeineCache(long duration, @NotNull TimeUnit unit) {
        this.cache = Caffeine.newBuilder().expireAfterAccess(duration, unit).build();
    }

    @Override
    public @NotNull V get(@NotNull K key, @NotNull Function<@NotNull K, @NotNull V> loader) {
        return this.cache.get(key, loader);
    }

    @Override
    public void put(@NotNull K key, @NotNull V value) {
        this.cache.put(key, value);
    }

    @Override
    public void invalidate(@NotNull K key) {
        this.cache.invalidate(key);
        this.cache.cleanUp();
    }
}
