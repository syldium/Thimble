package me.syldium.thimble.common.cache;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public interface CacheService<K, V> {

    @NotNull V get(@NotNull K key, @NotNull Function<@NotNull K, @NotNull V> loader);

    void put(@NotNull K key, @NotNull V value);

    void invalidate(@NotNull K key);

    static <K, V> @NotNull CacheService<K, V> create(long duration, @NotNull TimeUnit unit) {
        return CacheProvider.INSTANCE.create(duration, unit);
    }

    static <K, V> @NotNull CacheService<K, V> dummy() {
        return new DummyCache<>();
    }
}
