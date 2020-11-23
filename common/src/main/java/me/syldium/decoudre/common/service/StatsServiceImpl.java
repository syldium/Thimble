package me.syldium.decoudre.common.service;

import me.syldium.decoudre.api.Ranking;
import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.api.player.DePlayerStats;
import me.syldium.decoudre.api.service.StatsService;
import me.syldium.decoudre.api.util.Leaderboard;
import me.syldium.decoudre.common.player.PlayerStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StatsServiceImpl implements StatsService {

    private final Map<Ranking, Leaderboard<DePlayerStats>> leaderboard = new EnumMap<>(Ranking.class);

    private final DataService dataService;
    private final Executor executor;

    public StatsServiceImpl(@NotNull DataService dataService, @NotNull Executor executor) {
        this.dataService = dataService;
        this.executor = executor;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<DePlayerStats>> getPlayerStatistics(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.dataService.getPlayerStatistics(uuid), this.executor);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<@NotNull DePlayerStats>> getPlayerStatistics(@NotNull String name) {
        return CompletableFuture.supplyAsync(() -> this.dataService.getPlayerStatistics(name), this.executor);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> savePlayerStatistics(@NotNull DePlayerStats statistics) {
        return CompletableFuture.supplyAsync(() -> {
            this.dataService.savePlayerStatistics(statistics);
            return null;
        }, this.executor);
    }

    @Override
    public @NotNull Leaderboard<@NotNull DePlayerStats> getLeaderboard(@NotNull Ranking criteria) {
        return this.leaderboard.computeIfAbsent(criteria, s ->
                // SqlDataService is not currently thread-safe and the return could be used as a placeholder.
                CompletableFuture.supplyAsync(() -> this.dataService.getLeaderboard(criteria), this.executor).join()
        );
    }

    /**
     * Updates the cached leaderboards.
     *
     * @param stats The updated statistics.
     */
    public void updateLeaderboard(@NotNull DePlayerStats stats) {
        for (Leaderboard<DePlayerStats> leaderboard : this.leaderboard.values()) {
            leaderboard.add(stats);
        }
    }

    public void updateLeaderboard(@NotNull DePlayer player) {
        this.updateLeaderboard(new PlayerStats(
                player.uuid(),
                player.name(),
                player.getWins(),
                player.getLosses(),
                player.getJumps(),
                player.getDacs()
        ));
    }
}
