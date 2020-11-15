package me.syldium.decoudre.api.arena;

import me.syldium.decoudre.api.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an arena that players can join to play Dé À Coudre.
 */
public interface DeArena {

    /**
     * Gets the arena name.
     *
     * @return The arena name
     */
    @NotNull String getName();

    /**
     * Gets the location where players are teleported when they arrive in the pool and after a jump.
     *
     * @return The spawn location
     */
    @NotNull Location getSpawnLocation();

    /**
     * Sets the spawn location. {@link #getSpawnLocation()}
     *
     * @param location The new spawn location.
     */
    void setSpawnLocation(@NotNull Location location);

    /**
     * Gets the location where players are teleported to jump.
     *
     * @return The jump location.
     */
    @NotNull Location getJumpLocation();

    /**
     * Sets the jump location. {@link #getJumpLocation()}
     *
     * @param location The new jump location.
     */
    void setJumpLocation(@NotNull Location location);

    /**
     * Gets the minimum number of players to start a game.
     *
     * @return The minimum number.
     */
    @Range(from=1, to=Integer.MAX_VALUE) int getMinPlayers();

    /**
     * Sets the minimum number of players the pool can host.
     *
     * @param minimum The minimum players.
     * @throws IllegalArgumentException If negative or more than the maximum.
     */
    void setMinPlayers(int minimum) throws IllegalArgumentException;

    /**
     * Gets the maximum number of players that the arena can host.
     *
     * @return The maximum number.
     */
    @Range(from=1, to=Integer.MAX_VALUE) int getMaxPlayers();

    /**
     * Sets the maximum number of players the arena can host.
     *
     * @param maximum The maximum players.
     * @throws IllegalArgumentException If negative or less than the minimum.
     */
    void setMaxPlayers(int maximum) throws IllegalArgumentException;

    /**
     * Gets the current game.
     *
     * @return The game if exists.
     */
    @NotNull Optional<@NotNull DeGame> getGame();

    /**
     * Adds the player in the pool by creating a game if needed.
     *
     * @param player The player who want to play.
     * @return If the player has successfully joined the arena.
     */
    @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull UUID player);

    /**
     * Removes a player from the arena.
     *
     * @param player A player uuid.
     * @return If the player has left the arena.
     */
    boolean removePlayer(@NotNull UUID player);
}
