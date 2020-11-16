package me.syldium.decoudre.common.service;

import me.syldium.decoudre.api.player.DePlayerStats;
import me.syldium.decoudre.api.service.StatsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StatsServiceImpl implements StatsService {

    private final DataService dataService;
    private final Executor executor;

    public StatsServiceImpl(@NotNull DataService dataService, @NotNull Executor executor) {
        this.dataService = dataService;
        this.executor = executor;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Optional<DePlayerStats>> getPlayerStatistics(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.dataService.getPlayerStatistics(uuid), this.executor);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Void> savePlayerStatistics(@NotNull DePlayerStats statistics) {
        return CompletableFuture.supplyAsync(() -> {
            this.dataService.savePlayerStatistics(statistics);
            return null;
        }, this.executor);
    }
}
