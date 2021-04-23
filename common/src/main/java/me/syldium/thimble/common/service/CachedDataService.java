package me.syldium.thimble.common.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.util.Leaderboard;
import me.syldium.thimble.common.cache.CacheService;
import me.syldium.thimble.common.player.PlayerStats;
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
        this.stringCache = nameCacheDuration > 0 ? CacheService.create(nameCacheDuration, TimeUnit.SECONDS) : CacheService.dummy();
        this.uuidCache = uuidCacheDuration > 0 ? CacheService.create(uuidCacheDuration, TimeUnit.SECONDS) : CacheService.dummy();
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
        this.cache(statistics);
    }

    @Override
    public void close() {
        this.dataService.close();
    }

    void cache(@NotNull ThimblePlayerStats stats) {
        Optional<ThimblePlayerStats> optional = Optional.of(stats instanceof ThimblePlayer ? new PlayerStats(stats) : stats);
        this.stringCache.put(stats.name(), optional);
        this.uuidCache.put(stats.uuid(), optional);
    }

    void invalidate(@NotNull String playerName, @NotNull UUID playerUniqueId) {
        this.stringCache.invalidate(playerName);
        this.uuidCache.invalidate(playerUniqueId);
    }
}
