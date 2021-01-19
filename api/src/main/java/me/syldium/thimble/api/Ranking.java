package me.syldium.thimble.api;

import me.syldium.thimble.api.player.ThimblePlayerStats;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Various ranking criteria.
 */
public enum Ranking {

    /**
     * Depending on the number of victories.
     */
    WINS(ThimblePlayerStats::wins),

    /**
     * Based on the number of defeats.
     */
    LOSSES(ThimblePlayerStats::losses),

    /**
     * Per the number of successful jumps.
     */
    JUMPS(ThimblePlayerStats::jumps),

    /**
     * Per the number of failed jumps.
     */
    FAILS(ThimblePlayerStats::failedJumps),

    /**
     * According to the number of thimbles.
     */
    THIMBLES(ThimblePlayerStats::thimbles);

    private final Function<ThimblePlayerStats, Integer> getter;

    Ranking(@NotNull Function<ThimblePlayerStats, Integer> getter) {
        this.getter = getter;
    }

    /**
     * Returns the player's score using the criteria.
     *
     * @param stats An object of statistics.
     * @return A numeric value.
     */
    public int get(@NotNull ThimblePlayerStats stats) {
        return this.getter.apply(stats);
    }

    /**
     * Gets the integer value from stats.
     *
     * @return A function.
     */
    public @NotNull Function<ThimblePlayerStats, Integer> getter() {
        return this.getter;
    }
}
