package me.syldium.decoudre.api.player;

import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface DePlayerStats extends Identity {

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
     * Gets the number of combos made, i.e. the number of jumps between 4 solid blocks.
     *
     * @return The number of successful combos.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getDacs();
}
