package me.syldium.thimble.api.player;

import me.syldium.thimble.api.arena.ThimbleGame;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * The properties of a thimble player.
 */
public interface ThimblePlayer extends ThimblePlayerStats {

    /**
     * Gets the number of points - lifes in single mode.
     *
     * @return A number.
     */
    @Contract(pure = true)
    @Range(from=0, to=Integer.MAX_VALUE) int points();

    /**
     * Gets the number of successful jumps in this game.
     *
     * @return The number of successful jumps.
     */
    @Contract(pure = true)
    @Range(from=0, to=Integer.MAX_VALUE) int jumpsForGame();

    /**
     * Returns {@code true} if the player is a game spectator.
     *
     * @return If so.
     */
    @Contract(pure = true)
    boolean isSpectator();

    /**
     * Sets the player as a spectator of the game.
     *
     * <p>Spectators should not jump in the pool.</p>
     *
     * @param spectator {@code true} if he spectate.
     */
    void setSpectator(boolean spectator);

    /**
     * Returns {@code true} if the player is vanished.
     *
     * <p>A vanished player is not necessarily a spectator. If this player is invisible, he shouldn't appear in messages.</p>
     *
     * @return If so.
     */
    @Contract(pure = true)
    boolean isVanished();

    /**
     * Returns {@code true} if the player is jumping.
     *
     * @return If so.
     */
    @Contract(pure = true)
    boolean isJumping();

    /**
     * Gets the game the player is in.
     *
     * @return The game.
     */
    @Contract(pure = true)
    @NotNull ThimbleGame getGame();
}
