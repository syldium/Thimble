package me.syldium.decoudre.api.service;

import me.syldium.decoudre.api.player.DePlayerStats;
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
    @NotNull CompletableFuture<@NotNull Optional<@NotNull DePlayerStats>> getPlayerStatistics(@NotNull UUID uuid);

    /**
     * Fetch the player's stats from a username.
     *
     * @param name The player's name.
     * @return Statistics, if the player has already played.
     */
    @NotNull CompletableFuture<@NotNull Optional<@NotNull DePlayerStats>> getPlayerStatistics(@NotNull String name);

    /**
     * Save the statistics.
     *
     * @param statistics Statistics.
     * @return When the save has been made.
     */
    @NotNull CompletableFuture<@Nullable Void> savePlayerStatistics(@NotNull DePlayerStats statistics);
}
