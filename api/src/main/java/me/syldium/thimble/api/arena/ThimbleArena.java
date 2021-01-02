package me.syldium.thimble.api.arena;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an arena that players can join to play Dé À Coudre.
 */
public interface ThimbleArena extends ComponentLike {

    /**
     * Gets the arena name.
     *
     * @return The arena name.
     */
    @Contract(pure = true)
    @NotNull String getName();

    /**
     * Gets the location where players are teleported when they arrive in the pool and after a jump.
     *
     * @return The spawn location.
     */
    @Contract(pure = true)
    @Nullable Location getSpawnLocation();

    /**
     * Sets the spawn location. {@link #getSpawnLocation()}
     *
     * @param location The new spawn location.
     * @throws IllegalStateException If the argument is {@code null} and a game exists
     * @return This arena.
     */
    @Contract("_ -> this")
    ThimbleArena setSpawnLocation(@Nullable Location location);

    /**
     * Gets the location where players are teleported to jump.
     *
     * @return The jump location.
     */
    @Contract(pure = true)
    @Nullable Location getJumpLocation();

    /**
     * Sets the jump location. {@link #getJumpLocation()}
     *
     * @param location The new jump location.
     * @throws IllegalStateException If the argument is {@code null} and a game exists.
     * @return This arena.
     */
    @Contract("_ -> this")
    ThimbleArena setJumpLocation(@Nullable Location location);

    /**
     * Gets the location where the players wait their turn when the game has started.
     *
     * <p>When creating the arena using the commands, the wait location will first be identical to the spawn's position.</p>
     *
     * @return The wait location.
     */
    @Contract(pure = true)
    @Nullable Location getWaitLocation();

    /**
     * Sets the wait location. {@link #getWaitLocation()}
     *
     * @param location The new wait location.
     * @throws IllegalStateException If the argument is {@code null} and a game exists.
     * @return This arena.
     */
    @Contract("_ -> this")
    ThimbleArena setWaitLocation(@Nullable Location location);

    /**
     * Gets the minimum number of players to start a game.
     *
     * @return The minimum number.
     */
    @Contract(pure = true)
    @Range(from=1, to=Integer.MAX_VALUE) int getMinPlayers();

    /**
     * Sets the minimum number of players the pool can host.
     *
     * @param minimum The minimum players.
     * @throws IllegalArgumentException If negative or more than the maximum.
     * @return This arena.
     */
    @Contract("_ -> this")
    ThimbleArena setMinPlayers(int minimum) throws IllegalArgumentException;

    /**
     * Gets the maximum number of players that the arena can host.
     *
     * @return The maximum number.
     */
    @Contract(pure = true)
    @Range(from=1, to=Integer.MAX_VALUE) int getMaxPlayers();

    /**
     * Sets the maximum number of players the arena can host.
     *
     * @param maximum The maximum players.
     * @throws IllegalArgumentException If negative or less than the minimum.
     * @return This arena.
     */
    @Contract("_ -> this")
    ThimbleArena setMaxPlayers(int maximum) throws IllegalArgumentException;

    /**
     * Gets the current game.
     *
     * @return The game if exists.
     */
    @Contract(pure = true)
    @NotNull Optional<@NotNull ThimbleGame> getGame();

    /**
     * Returns the game mode that will be used for new games.
     *
     * @return The game mode.
     */
    @Contract(pure = true)
    @NotNull ThimbleGameMode getGameMode();

    /**
     * Sets the game mode for new games.
     *
     * @param gameMode A game mode.
     * @return This arena.
     */
    @Contract("_ -> this")
    ThimbleArena setGameMode(@NotNull ThimbleGameMode gameMode);

    /**
     * Adds the player in the pool by creating a game if needed.
     *
     * @param player The online player who want to play.
     * @return If the player has successfully joined the arena.
     * @throws IllegalStateException If the arena is not properly configured. {@link #isSetup()}
     */
    @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull UUID player);

    /**
     * Adds the {@link Identified} player in the pool by creating a game if needed.
     *
     * @param identified The {@link Identified} player.
     * @return If the player has successfully joined the arena.
     * @throws IllegalStateException If the arena is not properly configured. {@link #isSetup()}
     */
    default @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull Identified identified) {
        return this.addPlayer(identified.identity().uuid());
    }

    /**
     * Removes a player from the arena.
     *
     * @param player A player uuid.
     * @param teleport {@code true} if the player has to be teleported depending on the arena configuration.
     * @return If the player has left the arena.
     */
    boolean removePlayer(@NotNull UUID player, boolean teleport);

    /**
     * Removes an {@link Identified} player from the game.
     *
     * @param identified A player.
     * @param teleport {@code true} if the player has to be teleported depending on the arena configuration.
     * @return If the player has left the game.
     */
    default boolean removePlayer(@NotNull Identified identified, boolean teleport) {
        return this.removePlayer(identified.identity().uuid(), teleport);
    }

    /**
     * Returns whether the arena is properly configured.
     *
     * @return If no {@link Location} is {@code null}.
     */
    @Contract(pure = true)
    default boolean isSetup() {
        return this.getJumpLocation() != null && this.getSpawnLocation() != null && this.getWaitLocation() != null;
    }

    /**
     * Gets the lower point of the pool.
     *
     * @return The minimum point.
     */
    @Contract(pure = true)
    @Nullable BlockVector getPoolMinPoint();

    /**
     * Sets the lower point of the pool.
     *
     * @param point The minimum point.
     * @return This arena.
     */
    @Contract("_ -> this")
    ThimbleArena setPoolMinPoint(@Nullable BlockVector point);

    /**
     * Gets the upper point of the pool.
     *
     * @return The maximum point.
     */
    @Contract(pure = true)
    @Nullable BlockVector getPoolMaxPoint();

    /**
     * Sets the upper point of the pool.
     *
     * @param point The maximum point.
     * @return This arena.
     */
    @Contract("_ -> this")
    ThimbleArena setPoolMaxPoint(@Nullable BlockVector point);

    /**
     * Gets the signs leading to this arena.
     *
     * @return The sign positions.
     */
    @Contract(pure = true)
    @NotNull @UnmodifiableView Set<@NotNull BlockPos> getSigns();
}
