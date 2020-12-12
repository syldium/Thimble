package me.syldium.thimble.api;

import me.syldium.thimble.api.player.ThimblePlayerStats;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum Ranking {

    WINS(ThimblePlayerStats::getWins),
    LOSSES(ThimblePlayerStats::getLosses),
    JUMPS(ThimblePlayerStats::getJumps),
    THIMBLES(ThimblePlayerStats::getThimbles);

    private final Function<ThimblePlayerStats, Integer> getter;

    Ranking(@NotNull Function<ThimblePlayerStats, Integer> getter) {
        this.getter = getter;
    }

    public int get(@NotNull ThimblePlayerStats stats) {
        return this.getter.apply(stats);
    }

    public @NotNull Function<ThimblePlayerStats, Integer> getter() {
        return this.getter;
    }
}
