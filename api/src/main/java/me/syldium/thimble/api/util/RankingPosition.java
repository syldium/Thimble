package me.syldium.thimble.api.util;

import me.syldium.thimble.api.Ranking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * A position in a leaderboard.
 */
public class RankingPosition {

    private final Ranking ranking;
    private final int position;

    /**
     * Constructs a new position in a leaderboard.
     *
     * @param ranking The ranking value.
     * @param position The position in this leaderboard.
     */
    public RankingPosition(@NotNull Ranking ranking, @Range(from = 0, to = Leaderboard.MAX_LENGTH - 1) int position) {
        // noinspection ConstantConditions
        if (position < 0 || position >= Leaderboard.MAX_LENGTH) {
            throw new IllegalArgumentException("The position is not within the range of the leaderboard.");
        }

        this.ranking = ranking;
        this.position = position;
    }

    /**
     * Gets the ranking type.
     *
     * @return The {@link Ranking}.
     */
    public @NotNull Ranking ranking() {
        return this.ranking;
    }

    /**
     * Gets the position in the leaderboard.
     *
     * @return The position.
     */
    public @Range(from = 0, to = Leaderboard.MAX_LENGTH - 1) int position() {
        return this.position;
    }
}
