package me.syldium.decoudre.common.service;

import me.syldium.decoudre.api.Ranking;
import me.syldium.decoudre.api.player.DePlayerStats;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.api.util.Leaderboard;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface DataService {

    @NotNull Optional<@NotNull DePlayerStats> getPlayerStatistics(@NotNull UUID uuid);

    @NotNull Optional<@NotNull DePlayerStats> getPlayerStatistics(@NotNull String name);

    @NotNull Leaderboard<@NotNull DePlayerStats> getLeaderboard(@NotNull Ranking ranking);

    void savePlayerStatistics(@NotNull DePlayerStats statistics);

    void close();

    static @NotNull DataService fromConfig(@NotNull DeCoudrePlugin plugin, @NotNull MainConfig config) {
        return SqlDataService.fromConfig(plugin, config);
    }

    enum Type {
        SQLITE,
        MYSQL,
        POSTGRE
    }
}
