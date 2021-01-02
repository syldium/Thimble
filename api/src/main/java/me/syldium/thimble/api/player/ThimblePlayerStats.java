package me.syldium.thimble.api.player;

import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * The cumulative statistics of a thimble player.
 */
public interface ThimblePlayerStats extends Identity {

    /**
     * Gets the player name.
     *
     * @return The player name.
     */
    @NotNull String name();

    /**
     * If the player has already won or lost a game.
     *
     * @return If so.
     */
    default boolean hasPlayed() {
        return this.getWins() > 0 || this.getLosses() > 0;
    }

    /**
     * Gets the number of victories.
     *
     * @return A number.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getWins();

    /**
     * Gets the number of defeats.
     *
     * @return The number of losses.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getLosses();

    /**
     * Gets the number of games won added to those lost.
     *
     * @return The total number of games played.
     */
    @Range(from=0, to=Integer.MAX_VALUE) default int getGamesPlayed() {
        return this.getWins() + this.getLosses();
    }

    /**
     * Gets the number of successful jumps.
     *
     * @return The number of successful jumps.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getJumps();

    /**
     * Gets the number of failed jumps.
     *
     * @return The number of failed jumps.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getFailedJumps();

    /**
     * Gets the number of "thimbles" made, i.e. the number of jumps between 4 solid blocks.
     *
     * @return The number of successful thimbles.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getThimbles();

    /**
     * Returns {@code true} whether it's the same player. {@link java.util.UUID#equals(Object)}
     *
     * @param o An other player.
     * @return If so.
     */
    boolean equalsPlayer(@NotNull ThimblePlayerStats o);
}
