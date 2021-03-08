package me.syldium.thimble.api.arena;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.api.util.BlockVector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A Dé à coudre game.
 *
 * @see ThimbleSingleGame One by one gamemode
 * @see ThimbleConcurrentGame Chaotic gamemode
 */
public interface ThimbleGame {

    /**
     * The arena.
     *
     * @return The game arena.
     */
    @Contract(pure = true)
    @NotNull ThimbleArena arena();

    /**
     * Gets the game state.
     *
     * @return The current game state.
     */
    @Contract(pure = true)
    @NotNull ThimbleState state();

    /**
     * Tests if a new player can join the game.
     *
     * <p>This method verifies that the game has not started and that the maximum number of players will not be exceeded
     * if a new player joins.</p>
     *
     * @return If so.
     */
    @Contract(pure = true)
    boolean acceptPlayer();

    /**
     * Tests if new players can join the game.
     *
     * @param count The number of players wanting to join the arena.
     * @return If so.
     * @throws IllegalArgumentException If {@code count} is negative.
     */
    @Contract(pure = true)
    boolean acceptPlayers(@Range(from = 0, to = Integer.MAX_VALUE) int count);

    /**
     * If the game can start.
     *
     * @return If so.
     */
    @Contract(pure = true)
    boolean canStart();

    /**
     * Gets a {@link Set} of players still alive and not spectators/vanished.
     *
     * @return An immutable set.
     */
    @Contract(pure = true)
    @NotNull @UnmodifiableView Set<@NotNull ThimblePlayer> alivePlayers();

    /**
     * Gets a set of players.
     *
     * <p>This set may contain spectator or vanished players.</p>
     *
     * @return An immutable set.
     */
    @Contract(pure = true)
    @NotNull @UnmodifiableView Set<@NotNull ThimblePlayer> players();

    /**
     * Adds a player to the game. {@link ThimbleArena#addPlayer(UUID)}
     *
     * <p>The player limit ({@link ThimbleArena#maxPlayers()}) can be exceeded.</p>
     *
     * <p>Example to safely add a player:</p>
     * <pre>
     * if (game.acceptPlayer()) {
     *     game.addPlayer(player);
     * }
     * </pre>
     *
     * @param player The online player who want to play.
     * @return If the player has successfully joined the arena.
     */
    @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull UUID player);

    /*/**
     * Adds an {@link Identified} player to the game. {@link #addPlayer(UUID)}
     *
     * <p>The player limit ({@link ThimbleArena#maxPlayers()}) can be exceeded.</p>
     *
     * @param identified A player.
     * @return If the player has successfully joined the arena.
     */
    /*default @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull Identified identified) {
        return this.addPlayer(identified.identity().uuid());
    }*/

    /**
     * Removes a player from the game.
     *
     * @param player A player uuid.
     * @return If the player has left the game.
     */
    default boolean removePlayer(@NotNull UUID player) {
        return this.removePlayer(player, true);
    }

    /**
     * Removes a player from the game.
     *
     * @param player A player uuid.
     * @param teleport {@code true} if the player has to be teleported depending on the arena configuration.
     * @return If the player has left the game.
     */
    boolean removePlayer(@NotNull UUID player, boolean teleport);

    /*/**
     * Removes an {@link Identified} player from the game.
     *
     * @param identified A player.
     * @param teleport {@code true} if the player has to be teleported depending on the arena configuration.
     * @return If the player has left the game.
     */
    /*default boolean removePlayer(@NotNull Identified identified, boolean teleport) {
        return this.removePlayer(identified.identity().uuid(), teleport);
    }*/

    /**
     * Defines the jump result of the current jumper.
     *
     * @param playerUUID The player's unique identifier.
     * @param verdict The jump result.
     * @return If all went well.
     * @throws IllegalArgumentException If the player is not in the arena.
     * @throws IllegalStateException If no players is currently jumping.
     */
    boolean verdict(@NotNull UUID playerUUID, @NotNull JumpVerdict verdict);

    /**
     * Returns {@code true} if no player is in the game.
     *
     * @return If empty.
     */
    @Contract(pure = true)
    boolean isEmpty();

    /**
     * Returns the number of players in the game - vanished players are ignored.
     *
     * @return The number of non vanished players.
     */
    @Contract(pure = true)
    int size();

    /**
     * Returns the number of players in the game.
     *
     * @return The number of players.
     */
    @Contract(pure = true)
    int realSize();

    /**
     * Gets the player audience.
     *
     * @return The audience.
     */
    @NotNull Set<UUID> uuidSet();

    /**
     * Returns {@code true} if the player is jumping.
     *
     * @param playerUUID The player's unique identifier.
     * @return If so.
     */
    @Contract(pure = true)
    boolean isJumping(@NotNull UUID playerUUID);

    /**
     * Returns {@code true} if the player is jumping.
     *
     * @param player The player.
     * @return If so.
     */
    @Contract(pure = true)
    default boolean isJumping(@NotNull ThimblePlayer player) {
        return this.isJumping(player.uuid());
    }

    /**
     * Returns the position of known remaining water blocks.
     *
     * @return The remaining block vectors.
     * @throws IllegalStateException If the pool dimensions have not been defined.
     */
    @Contract(pure = true)
    @NotNull Set<@NotNull BlockVector> remainingWaterBlocks();

    /**
     * Returns {@code true} if the pool is full.
     *
     * @return {@code true} if the pool is full.
     * @throws IllegalStateException If the pool dimensions have not been defined.
     */
    @Contract(pure = true)
    default boolean isPoolFull() {
        return this.remainingWaterBlocks().size() < 1;
    }

    /**
     * Returns if the player's inventory should be cleared when joining the game.
     *
     * @return {@code true} if the inventory should be cleared.
     * @since 1.0.2
     */
    @Contract(pure = true)
    boolean shouldClearInventory();

    /**
     * Returns if players become spectators when they lose.
     *
     * @return {@code true} if the spectator mode is active.
     * @since 1.0.2
     */
    @Contract(pure = true)
    boolean hasSpectatorMode();
}
