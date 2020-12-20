package me.syldium.thimble.api.util;

import me.syldium.thimble.api.Ranking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class RankingPosition {

    private final Ranking ranking;
    private final int position;

    public RankingPosition(@NotNull Ranking ranking, @Range(from = 0, to = Leaderboard.MAX_LENGTH - 1) int position) {
        // noinspection ConstantConditions
        if (position < 0 || position >= Leaderboard.MAX_LENGTH) {
            throw new IllegalArgumentException("The position is not within the range of the leaderboard.");
        }

        this.ranking = ranking;
        this.position = position;
    }

    public @NotNull Ranking getRanking() {
        return this.ranking;
    }

    public @Range(from = 0, to = Leaderboard.MAX_LENGTH - 1) int getPosition() {
        return this.position;
    }
}
