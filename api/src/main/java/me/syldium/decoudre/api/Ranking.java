package me.syldium.decoudre.api;

import me.syldium.decoudre.api.player.DePlayerStats;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum Ranking {

    WINS(DePlayerStats::getWins),
    LOSSES(DePlayerStats::getLosses),
    JUMPS(DePlayerStats::getJumps),
    DACS(DePlayerStats::getDacs);

    private final Function<DePlayerStats, Integer> getter;

    Ranking(@NotNull Function<DePlayerStats, Integer> getter) {
        this.getter = getter;
    }

    public int get(@NotNull DePlayerStats stats) {
        return this.getter.apply(stats);
    }

    public @NotNull Function<DePlayerStats, Integer> getter() {
        return this.getter;
    }
}
