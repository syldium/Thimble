package me.syldium.decoudre.api.arena;

import me.syldium.decoudre.api.player.DePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A Dé à coudre game.
 */
public interface DeGame {

    /**
     * The arena.
     *
     * @return The game arena.
     */
    @NotNull DeArena getArena();

    /**
     * Gets the game state.
     *
     * @return The current game state.
     */
    @NotNull DeState getState();

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
     * Gets a {@link Set} of players still alive.
     *
     * @return An immutable set.
     */
    @NotNull @UnmodifiableView Set<@NotNull DePlayer> getAlivePlayers();

    /**
     * Gets a set of players.
     *
     * @return An immutable set.
     */
    @NotNull @UnmodifiableView Set<@NotNull DePlayer> getPlayers();

    /**
     * Adds a player to the game. {@link DeArena#addPlayer(UUID)}
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
     * Gets the player to whom it is the turn to jump.
     *
     * @return The jumper, if any.
     */
    @Nullable UUID getCurrentJumper();

    /**
     * Returns the player who will jump right after.
     *
     * @return The jumper, if any.
     */
    @Nullable UUID peekNextJumper();
}
