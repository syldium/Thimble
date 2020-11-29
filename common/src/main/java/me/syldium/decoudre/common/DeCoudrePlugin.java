package me.syldium.decoudre.common;

import me.syldium.decoudre.common.adapter.EventAdapter;
import me.syldium.decoudre.common.adapter.PlayerAdapter;
import me.syldium.decoudre.common.config.ArenaConfig;
import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.service.DataService;
import me.syldium.decoudre.common.service.GameServiceImpl;
import me.syldium.decoudre.common.service.MessageService;
import me.syldium.decoudre.common.service.MessageServiceImpl;
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
    private MessageService messageService;
    private StatsServiceImpl statsService;
    private MainConfig config;

    public void enable(@NotNull MainConfig config) {
        this.dataService = DataService.fromConfig(this, config);
        this.gameService = new GameServiceImpl(this, this.getArenaConfig());
        this.messageService = new MessageServiceImpl(config, this.getLogger());
        this.statsService = new StatsServiceImpl(this.dataService, Executors.newSingleThreadExecutor());
        this.config = config;
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

    public static boolean classExists(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public abstract @NotNull Logger getLogger();

    public abstract @NotNull ArenaConfig getArenaConfig();

    public @NotNull MainConfig getMainConfig() {
        return this.config;
    }

    public abstract @NotNull File getDataFolder();

    public abstract @NotNull Task startGameTask(@NotNull Runnable runnable);

    public @NotNull GameServiceImpl getGameService() {
        return this.gameService;
    }

    public @NotNull MessageService getMessageService() {
        return this.messageService;
    }

    public @NotNull StatsServiceImpl getStatsService() {
        return this.statsService;
    }

    public abstract @Nullable Player getPlayer(@NotNull UUID uuid);

    public abstract @NotNull EventAdapter<?> getEventAdapter();

    public abstract @NotNull PlayerAdapter<?, ?> getPlayerAdapter();
}
