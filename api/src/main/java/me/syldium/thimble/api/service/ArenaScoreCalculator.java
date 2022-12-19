package me.syldium.thimble.api.service;

import me.syldium.thimble.api.arena.ThimbleArena;
import org.jetbrains.annotations.NotNull;

/**
 * An arena selection strategy.
 *
 * @since 1.5.0
 */
@FunctionalInterface
public interface ArenaScoreCalculator {

    /**
     * Determines the match score of the arena.
     *
     * <p>When an arena may accept this number of players, a score
     * reflecting the suitability level for this arena is calculated.</p>
     *
     * @param arena The arena.
     * @param playersCount The number of players who want to join.
     * @return The arena score, lower is better.
     */
    int calculateScore(@NotNull ThimbleArena arena, int playersCount);
}
