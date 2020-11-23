package me.syldium.decoudre.api.util;

import me.syldium.decoudre.api.Ranking;
import org.jetbrains.annotations.NotNull;

public class RankingPosition {

    private final Ranking ranking;
    private final int position;

    public RankingPosition(@NotNull Ranking ranking, int position) {
        if (position < 0 || position >= Leaderboard.MAX_LENGTH) {
            throw new IllegalArgumentException("The position is not within the range of the leaderboard.");
        }

        this.ranking = ranking;
        this.position = position;
    }

    public @NotNull Ranking getRanking() {
        return this.ranking;
    }

    public int getPosition() {
        return this.position;
    }
}
