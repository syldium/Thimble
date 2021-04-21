package me.syldium.thimble.common.cache;

import java.util.concurrent.TimeUnit;

@FunctionalInterface
interface CacheProvider {

    CacheProvider INSTANCE = new Impl();

    <K, V> CacheService<K,V> create(long duration, TimeUnit unit);

    final class Impl implements CacheProvider {

        private final CacheProvider provider;

        Impl() {
            CacheProvider provider = DummyCache::new;
            try {
                Class.forName("com.github.benmanes.caffeine.cache.Cache");
                provider = CaffeineCache::new;
            } catch (ClassNotFoundException ex) {
                try {
                    Class.forName("com.google.common.cache.Cache");
                    provider = GuavaCache::new;
                } catch (ClassNotFoundException ignored) { }
            }
            this.provider = provider;
        }

        @Override
        public <K, V> CacheService<K, V> create(long duration, TimeUnit unit) {
            return this.provider.create(duration, unit);
        }
    }
}
