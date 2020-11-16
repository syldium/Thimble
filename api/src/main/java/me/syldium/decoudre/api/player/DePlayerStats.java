package me.syldium.decoudre.api.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.UUID;

public interface DePlayerStats {

    /**
     * Get the unique identifier assigned to the player.
     *
     * @return The UUID.
     */
    @NotNull UUID uuid();

    /**
     * If the player has already won or lost a game.
     *
     * @return If so.
     */
    default boolean hasPlayed() {
        return this.getWins() > 0 || this.getLosses() > 0;
    }

    /**
     * Get the number of victories.
     *
     * @return A number.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getWins();

    /**
     * Get the number of defeats.
     *
     * @return The number of losses.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getLosses();

    /**
     * Get the number of games won added to those lost.
     *
     * @return The total number of games played.
     */
    @Range(from=0, to=Integer.MAX_VALUE) default int getGamesPlayed() {
        return this.getWins() + this.getLosses();
    }

    /**
     * Get the number of successful jumps.
     *
     * @return The number of successful jumps.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getJumps();

    /**
     * Get the number of combos made, i.e. the number of jumps between 4 solid blocks.
     *
     * @return The number of successful combos.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getDacs();
}
