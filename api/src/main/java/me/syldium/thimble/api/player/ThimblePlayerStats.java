package me.syldium.thimble.api.player;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
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
    @Contract(pure = true)
    @NotNull String name();

    default @NotNull Component displayName() {
        return Component.text(this.name());
    }

    /**
     * If the player has already won or lost a game.
     *
     * @return If so.
     */
    @Contract(pure = true)
    default boolean hasPlayed() {
        return this.wins() > 0 || this.losses() > 0;
    }

    /**
     * Gets the number of victories.
     *
     * @return A number.
     */
    @Contract(pure = true)
    @Range(from=0, to=Integer.MAX_VALUE) int wins();

    /**
     * Gets the number of defeats.
     *
     * @return The number of losses.
     */
    @Contract(pure = true)
    @Range(from=0, to=Integer.MAX_VALUE) int losses();

    /**
     * Gets the number of games won added to those lost.
     *
     * @return The total number of games played.
     */
    @Contract(pure = true)
    @Range(from=0, to=Integer.MAX_VALUE) default int gamesPlayed() {
        return this.wins() + this.losses();
    }

    /**
     * Gets the number of successful jumps.
     *
     * @return The number of successful jumps.
     */
    @Contract(pure = true)
    @Range(from=0, to=Integer.MAX_VALUE) int jumps();

    /**
     * Gets the number of failed jumps.
     *
     * @return The number of failed jumps.
     */
    @Contract(pure = true)
    @Range(from=0, to=Integer.MAX_VALUE) int failedJumps();

    /**
     * Gets the number of "thimbles" made, i.e. the number of jumps between 4 solid blocks.
     *
     * @return The number of successful thimbles.
     */
    @Contract(pure = true)
    @Range(from=0, to=Integer.MAX_VALUE) int thimbles();

    /**
     * Returns {@code true} whether it's the same player. {@link java.util.UUID#equals(Object)}
     *
     * @param o An other player.
     * @return If so.
     */
    @Contract(pure = true)
    boolean equalsPlayer(@NotNull ThimblePlayerStats o);
}
