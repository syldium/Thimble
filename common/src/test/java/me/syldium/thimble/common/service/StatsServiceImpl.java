package me.syldium.thimble.common.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.api.util.Leaderboard;
import me.syldium.thimble.common.player.PlayerStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Overrides {@link StatsServiceImpl}.
 */
public class StatsServiceImpl implements StatsService {

    private final Map<Ranking, Leaderboard<ThimblePlayerStats>> leaderboard = new EnumMap<>(Ranking.class);
    private final Map<UUID, ThimblePlayerStats> statistics = new HashMap<>();

    public StatsServiceImpl(@NotNull DataService dataService, @NotNull Executor executor) {

    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<ThimblePlayerStats>> getPlayerStatistics(@NotNull UUID uuid) {
        return CompletableFuture.completedFuture(Optional.ofNullable(this.statistics.get(uuid)));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<@NotNull ThimblePlayerStats>> getPlayerStatistics(@NotNull String name) {
        for (ThimblePlayerStats stats : this.statistics.values()) {
            if (stats.name().equals(name)) {
                return CompletableFuture.completedFuture(Optional.of(stats));
            }
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> savePlayerStatistics(@NotNull ThimblePlayerStats statistics) {
        this.statistics.put(statistics.uuid(), statistics);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull Leaderboard<@NotNull ThimblePlayerStats> getLeaderboard(@NotNull Ranking criteria) {
        return this.leaderboard.computeIfAbsent(criteria, s -> Leaderboard.of(criteria));
    }

    public void updateLeaderboard(@NotNull ThimblePlayerStats stats) {
        for (Leaderboard<ThimblePlayerStats> leaderboard : this.leaderboard.values()) {
            leaderboard.add(stats);
        }
    }

    public void updateLeaderboard(@NotNull ThimblePlayer player) {
        this.updateLeaderboard(new PlayerStats(
                player.uuid(),
                player.name(),
                player.getWins(),
                player.getLosses(),
                player.getJumps(),
                player.getFailedJumps(),
                player.getThimbles()
        ));
    }
}
