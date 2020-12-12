package me.syldium.thimble.api.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.util.RankingPosition;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.util.Leaderboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StatsService {

    /**
     * Fetch the player's stats from a {@link UUID}.
     *
     * @param uuid The player's unique identifier.
     * @return Statistics, if the player has already played.
     */
    @NotNull CompletableFuture<@NotNull Optional<@NotNull ThimblePlayerStats>> getPlayerStatistics(@NotNull UUID uuid);

    /**
     * Fetch the player's stats from a username.
     *
     * @param name The player's name.
     * @return Statistics, if the player has already played.
     */
    @NotNull CompletableFuture<@NotNull Optional<@NotNull ThimblePlayerStats>> getPlayerStatistics(@NotNull String name);

    /**
     * Saves the statistics.
     *
     * @param statistics Statistics.
     * @return When the save has been made.
     */
    @NotNull CompletableFuture<@Nullable Void> savePlayerStatistics(@NotNull ThimblePlayerStats statistics);

    /**
     * Gets the leaderboard according to a ranking criterion.
     *
     * <p>The first call will block the current thread, then the following ones will get the cached results.</p>
     *
     * @param criteria The criteria.
     * @return The leaderboard.
     */
    @NotNull Leaderboard<@NotNull ThimblePlayerStats> getLeaderboard(@NotNull Ranking criteria);

    /**
     * Returns the player at this position in the leaderboard.
     *
     * @param position The criteria and the position.
     * @return The player statistics, if so
     */
    default @Nullable ThimblePlayerStats getLeaderboard(@NotNull RankingPosition position) {
        Leaderboard<ThimblePlayerStats> leaderboard = this.getLeaderboard(position.getRanking());
        return leaderboard.size() > position.getPosition() ? leaderboard.get(position.getPosition()) : null;
    }
}
