package me.syldium.thimble.api.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.util.RankingPosition;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.util.Leaderboard;
import net.kyori.adventure.identity.Identified;
import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The player statistics manager.
 * <pre>
 * Bukkit.getServicesManager().load(StatsService.class); // Bukkit
 * Sponge.getServiceManager().provide(StatsService.class); // Sponge
 * </pre>
 */
public interface StatsService {

    /**
     * Fetch the player's stats from a {@link UUID}.
     *
     * @param uuid The player's unique identifier.
     * @return Statistics, if the player has already played.
     */
    @NonBlocking @NotNull CompletableFuture<@NotNull Optional<@NotNull ThimblePlayerStats>> getPlayerStatistics(@NotNull UUID uuid);

    /**
     * Fetch the player's stats from a username.
     *
     * @param name The player's name.
     * @return Statistics, if the player has already played.
     */
    @NonBlocking @NotNull CompletableFuture<@NotNull Optional<@NotNull ThimblePlayerStats>> getPlayerStatistics(@NotNull String name);

    /**
     * Fetch the player's stats from an {@link net.kyori.adventure.identity.Identity}.
     *
     * @param identified The player.
     * @return Statistics, if the player has already played.
     */
    default @NonBlocking @NotNull CompletableFuture<@NotNull Optional<@NotNull ThimblePlayerStats>> getPlayerStatistics(@NotNull Identified identified) {
        return this.getPlayerStatistics(identified.identity().uuid());
    }

    /**
     * Saves the statistics.
     *
     * @param statistics Statistics.
     * @return When the save has been made.
     */
    @NonBlocking @NotNull CompletableFuture<@Nullable Void> savePlayerStatistics(@NotNull ThimblePlayerStats statistics);

    /**
     * Gets the leaderboard according to a ranking criterion.
     *
     * <p>The first call will block the current thread, then the following ones will get the cached results.</p>
     *
     * @param criteria The criteria.
     * @return The leaderboard.
     */
    @NotNull Leaderboard<ThimblePlayerStats> getLeaderboard(@NotNull Ranking criteria);

    /**
     * Returns the player at this position in the leaderboard.
     *
     * @param position The criteria and the position.
     * @return The player statistics, if so
     */
    default @Nullable ThimblePlayerStats getLeaderboard(@NotNull RankingPosition position) {
        Leaderboard<ThimblePlayerStats> leaderboard = this.getLeaderboard(position.ranking());
        return leaderboard.size() > position.position() ? leaderboard.get(position.position()) : null;
    }
}
