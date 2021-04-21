package me.syldium.thimble.common.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.util.Leaderboard;
import me.syldium.thimble.common.cache.CacheService;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

class CachedDataService implements DataService {

    private final CacheService<UUID, Optional<ThimblePlayerStats>> uuidCache;
    private final CacheService<String, Optional<ThimblePlayerStats>> stringCache;
    private final DataService dataService;

    CachedDataService(@NotNull DataService dataService) {
        this.dataService = dataService;
        this.uuidCache = CacheService.dummy();
        this.stringCache = CacheService.dummy();
    }

    CachedDataService(@NotNull SqlDataService dataService, int nameCacheDuration, int uuidCacheDuration) {
        this.dataService = dataService;
        this.stringCache = CacheService.create(nameCacheDuration, TimeUnit.SECONDS);
        this.uuidCache = CacheService.create(uuidCacheDuration, TimeUnit.SECONDS);
    }

    @Override
    public @NotNull Optional<@NotNull ThimblePlayerStats> getPlayerStatistics(@NotNull UUID uuid) {
        return this.uuidCache.get(uuid, this.dataService::getPlayerStatistics);
    }

    @Override
    public @NotNull Optional<@NotNull ThimblePlayerStats> getPlayerStatistics(@NotNull String name) {
        return this.stringCache.get(name, this.dataService::getPlayerStatistics);
    }

    @Override
    public @NotNull Leaderboard getLeaderboard(@NotNull Ranking ranking) {
        return this.dataService.getLeaderboard(ranking);
    }

    @Override
    public void savePlayerStatistics(@NotNull ThimblePlayerStats statistics) {
        this.dataService.savePlayerStatistics(statistics);
        Optional<ThimblePlayerStats> optional = Optional.of(statistics);
        this.stringCache.put(statistics.name(), optional);
        this.uuidCache.put(statistics.uuid(), optional);
    }

    @Override
    public void close() {
        this.dataService.close();
    }

    public void invalidate(@NotNull String playerName, @NotNull UUID playerUniqueId) {
        this.stringCache.invalidate(playerName);
        this.uuidCache.invalidate(playerUniqueId);
    }
}
