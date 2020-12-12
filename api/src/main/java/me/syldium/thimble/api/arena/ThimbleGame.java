package me.syldium.thimble.api.arena;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.player.JumpVerdict;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
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
    @NotNull ThimbleArena getArena();

    /**
     * Gets the game state.
     *
     * @return The current game state.
     */
    @NotNull ThimbleGameState getState();

    /**
     * Tests if new players can join the game.
     *
     * @return If so.
     */
    boolean acceptPlayers();

    /**
     * If the game can start.
     *
     * @return If so.
     */
    boolean canStart();

    /**
     * Gets a {@link Set} of players still alive and not spectators.
     *
     * @return An immutable set.
     */
    @NotNull @UnmodifiableView Set<@NotNull ThimblePlayer> getAlivePlayers();

    /**
     * Gets a set of players.
     *
     * @return An immutable set.
     */
    @NotNull @UnmodifiableView Set<@NotNull ThimblePlayer> getPlayers();

    /**
     * Adds a player to the game. {@link ThimbleArena#addPlayer(UUID)}
     *
     * @param player The online player who want to play.
     * @return If the player has successfully joined the arena.
     */
    @NotNull CompletableFuture<@NotNull Boolean> addPlayer(@NotNull UUID player);

    /**
     * Removes a player from the game.
     *
     * @param player A player uuid.
     * @return If the player has left the game.
     */
    boolean removePlayer(@NotNull UUID player);

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
    boolean isEmpty();

    /**
     * Returns the number of players in the game.
     *
     * @return The number of players.
     */
    int size();

    /**
     * Gets the player audience.
     *
     * @return The audience.
     */
    @NotNull Audience audience();

    /**
     * Returns {@code true} if the player is jumping.
     *
     * @param playerUUID The player's unique identifier.
     * @return If so.
     */
    boolean isJumping(@NotNull UUID playerUUID);

    /**
     * Returns {@code true} if the player is jumping.
     *
     * @param player The player.
     * @return If so.
     */
    default boolean isJumping(@NotNull ThimblePlayer player) {
        return this.isJumping(player.uuid());
    }

    /**
     * Returns the number of known remaining water blocks.
     *
     * @return The remaining number.
     * @throws IllegalStateException If the pool dimensions have not been defined.
     */
    int getRemainingWaterBlocks();

    /**
     * Returns {@code true} if the pool is full.
     *
     * @return {@code true} if the pool is full.
     * @throws IllegalStateException If the pool dimensions have not been defined.
     */
    default boolean isPoolFull() {
        return this.getRemainingWaterBlocks() < 1;
    }
}
