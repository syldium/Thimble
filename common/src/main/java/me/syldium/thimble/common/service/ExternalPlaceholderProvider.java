package me.syldium.thimble.common.service;

import me.syldium.thimble.api.Ranking;
import me.syldium.thimble.api.player.ThimblePlayerStats;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.api.util.RankingPosition;
import me.syldium.thimble.common.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;

public class ExternalPlaceholderProvider {

    private final Supplier<StatsService> statsService;

    public ExternalPlaceholderProvider(@NotNull Supplier<StatsService> statsService) {
        this.statsService = statsService;
    }

    public @Nullable String provide(@NotNull UUID playerUniqueId, @NotNull String tokens) {
        return this.provide(playerUniqueId, StringUtil.split(tokens, '_'));
    }

    public @Nullable String provide(@NotNull UUID playerUniqueId, @NotNull List<String> tokens) {
        if (tokens.size() < 2 || tokens.size() > 4) {
            return null;
        }

        if ("lb".equals(tokens.get(0))) {
            RankingPosition rankingPosition = this.parseRankingPosition(tokens.get(1), tokens.size() > 2 ? tokens.get(2) : "0");
            if (rankingPosition == null) {
                return null;
            }

            boolean requestUsername = tokens.size() > 3 && "name".equals(tokens.get(3));
            return this.formatPlayerStats(this.statsService.get().getLeaderboard(rankingPosition), rankingPosition.ranking(), requestUsername);
        }

        return null;
    }

    private @Nullable RankingPosition parseRankingPosition(@NotNull String ranking, @NotNull String position) {
        try {
            return new RankingPosition(Ranking.valueOf(ranking.toUpperCase(Locale.ROOT)), Integer.parseInt(position));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private @NotNull String formatPlayerStats(@Nullable ThimblePlayerStats stats, @NotNull Ranking ranking, boolean username) {
        if (stats == null) {
            return username ? "player" : "0";
        }
        return username ? stats.name() : String.valueOf(ranking.get(stats));
    }
}
