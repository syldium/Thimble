package me.syldium.thimble.common.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.api.util.Leaderboard;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.player.PlayerStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StatsServiceImpl implements StatsService, AutoCloseable {

    private final Map<Ranking, Leaderboard> leaderboard = new EnumMap<>(Ranking.class);

    private final DataService cachedDataService;
    private final SqlDataService dataService;
    private final Executor executor;

    public StatsServiceImpl(@NotNull SqlDataService dataService, @NotNull Executor executor, @NotNull ConfigNode cacheNode) {
        this(dataService, executor, cacheNode.getInt("name-duration", 60), cacheNode.getInt("uuid-duration", 120));
    }

    public StatsServiceImpl(@NotNull SqlDataService dataService, @NotNull Executor executor, int nameCacheDuration, int uuidCacheDuration) {
        this.cachedDataService = new CachedDataService(dataService, nameCacheDuration, uuidCacheDuration);
        this.dataService = dataService;
        this.executor = executor;
    }

    @TestOnly
    public StatsServiceImpl(@NotNull SqlDataService dataService, @NotNull Executor executor) {
        this.cachedDataService = dataService;
        this.dataService = dataService;
        this.executor = executor;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<ThimblePlayerStats>> getPlayerStatistics(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.cachedDataService.getPlayerStatistics(uuid), this.executor);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<@NotNull ThimblePlayerStats>> getPlayerStatistics(@NotNull String name) {
        return CompletableFuture.supplyAsync(() -> this.cachedDataService.getPlayerStatistics(name), this.executor);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> savePlayerStatistics(@NotNull ThimblePlayerStats statistics) {
        return CompletableFuture.supplyAsync(() -> {
            this.cachedDataService.savePlayerStatistics(statistics);
            return null;
        }, this.executor);
    }

    public <S extends ThimblePlayerStats> void savePlayerStatistics(@NotNull Iterable<S> iterable) {
        this.executor.execute(() -> {
            try {
                this.dataService.startBatch();
                for (S statistics : iterable) {
                    this.dataService.persist(statistics);
                }
                this.dataService.finishBatch();
            } catch (SQLException ex) {
                throw new RuntimeException("Unable to save statistics.", ex);
            }
        });
    }

    @Override
    public @NotNull Leaderboard getLeaderboard(@NotNull Ranking criteria) {
        return this.leaderboard.computeIfAbsent(criteria, this::fetchLeaderboard);
    }

    private @NotNull Leaderboard fetchLeaderboard(@NotNull Ranking criteria) {
        // SqlDataService is not currently thread-safe and the return could be used as a placeholder.
        return CompletableFuture.supplyAsync(() -> this.dataService.getLeaderboard(criteria), this.executor).join();
    }

    /**
     * Updates the cached leaderboards.
     *
     * @param stats The updated statistics.
     */
    public void updateLeaderboard(@NotNull ThimblePlayerStats stats) {
        for (Leaderboard leaderboard : this.leaderboard.values()) {
            leaderboard.add(stats);
        }
    }

    public void updateLeaderboard(@NotNull ThimblePlayer player) {
        this.updateLeaderboard(new PlayerStats(
                player.uuid(),
                player.name(),
                player.wins(),
                player.losses(),
                player.jumps(),
                player.failedJumps(),
                player.thimbles()
        ));
    }

    @Override
    public void close() {
        this.dataService.close();
        this.cachedDataService.close();
    }
}
