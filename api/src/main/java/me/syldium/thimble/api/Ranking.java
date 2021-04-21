package me.syldium.thimble.api;

import me.syldium.thimble.api.player.ThimblePlayerStats;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.ToIntFunction;

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

    private final ToIntFunction<ThimblePlayerStats> getter;

    Ranking(@NotNull ToIntFunction<ThimblePlayerStats> getter) {
        this.getter = getter;
    }

    /**
     * Returns the player's score using the criteria.
     *
     * @param stats An object of statistics.
     * @return A numeric value.
     */
    public int get(@NotNull ThimblePlayerStats stats) {
        return this.getter.applyAsInt(stats);
    }

    /**
     * Gets the integer value from stats.
     *
     * @return A function.
     */
    public @NotNull ToIntFunction<ThimblePlayerStats> getter() {
        return this.getter;
    }

    /**
     * Get the ranking from a string using {@link Ranking#valueOf(String)}.
     * 
     * @param string The name of the ranking constant to return.
     * @return The enum constant.
     * @throws IllegalArgumentException If no constant has this name.
     */
    public static @NotNull Ranking from(@NotNull String string) {
        return Ranking.valueOf(string.toUpperCase(Locale.ROOT));
    }
}
