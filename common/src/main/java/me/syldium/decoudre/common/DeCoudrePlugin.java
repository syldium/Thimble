package me.syldium.decoudre.common;

import me.syldium.decoudre.common.adapter.PlayerAdapter;
import me.syldium.decoudre.common.config.ArenaConfig;
import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.service.DataService;
import me.syldium.decoudre.common.service.GameServiceImpl;
import me.syldium.decoudre.common.service.StatsServiceImpl;
import me.syldium.decoudre.common.util.Task;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public abstract class DeCoudrePlugin {

    public static final Component PREFIX = MiniMessage.get().parse("<gray>[<yellow>DAC</yellow>]</gray> ");

    private DataService dataService;
    private GameServiceImpl gameService;
    private StatsServiceImpl statsService;

    public void enable(@NotNull MainConfig config) {
        this.dataService = DataService.fromConfig(this, config);
        this.gameService = new GameServiceImpl(this, this.getArenaConfig());
        this.statsService = new StatsServiceImpl(this.dataService, Executors.newSingleThreadExecutor());
    }

    public void disable() {
        this.gameService.save();
        this.dataService.close();
    }

    public @NotNull File getFile(@NotNull String filename) {
        File file = new File(this.getDataFolder(), filename);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    this.getLogger().severe(String.format("Unable to create the %s file.", filename));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public abstract @NotNull Logger getLogger();

    public abstract @NotNull ArenaConfig getArenaConfig();

    public abstract @NotNull File getDataFolder();

    public abstract @NotNull Task startGameTask(@NotNull Runnable runnable);

    public @NotNull GameServiceImpl getGameService() {
        return this.gameService;
    }

    public @NotNull StatsServiceImpl getStatsService() {
        return this.statsService;
    }

    public abstract @Nullable Player getPlayer(@NotNull UUID uuid);

    public abstract @NotNull PlayerAdapter<?, ?> getPlayerAdapter();
}
