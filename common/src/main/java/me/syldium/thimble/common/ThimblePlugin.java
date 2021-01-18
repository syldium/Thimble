package me.syldium.thimble.common;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.Thimble;
import me.syldium.thimble.common.dependency.Dependency;
import me.syldium.thimble.common.dependency.DependencyInjection;
import me.syldium.thimble.common.dependency.DependencyResolver;
import me.syldium.thimble.common.util.ServerType;
import me.syldium.thimble.common.adapter.EventAdapter;
import me.syldium.thimble.common.adapter.PlayerAdapter;
import me.syldium.thimble.common.command.CommandManager;
import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.config.MainConfig;
import me.syldium.thimble.common.config.SavedPlayersManager;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.service.DataService;
import me.syldium.thimble.common.service.GameServiceImpl;
import me.syldium.thimble.common.service.MessageService;
import me.syldium.thimble.common.service.MessageServiceImpl;
import me.syldium.thimble.common.service.StatsServiceImpl;
import me.syldium.thimble.common.update.GitHubAssetInfo;
import me.syldium.thimble.common.update.UpdateChecker;
import me.syldium.thimble.common.util.Fireworks;
import me.syldium.thimble.common.util.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ThimblePlugin {

    private DataService dataService;
    private GameServiceImpl gameService;
    private MessageService messageService;
    private StatsServiceImpl statsService;
    private UpdateChecker updateChecker;

    public void enable() {
        if (!classExists("com.google.gson.JsonElement")) {
            Path gson = new DependencyResolver(this).downloadDependency(Dependency.GSON);
            DependencyInjection.addJarToClasspath(gson, this);
        }
        this.dataService = DataService.fromConfig(this, this.getMainConfig());
        this.gameService = new GameServiceImpl(this);
        this.messageService = new MessageServiceImpl(this);
        this.statsService = new StatsServiceImpl(this.dataService, Executors.newSingleThreadExecutor());
        this.updateChecker = new UpdateChecker(Thimble.pluginVersion(), this.getServerType(), this.getLogger());
        this.gameService.load();
        Thimble.setGameService(this.gameService);
        Thimble.setStatsService(this.statsService);
    }

    public void disable() {
        Thimble.setGameService(null);
        Thimble.setStatsService(null);
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

    public abstract @NotNull File getDataFolder();

    public abstract @NotNull Path getPluginPath();

    public abstract @NotNull ServerType getServerType();

    public abstract @NotNull Task startGameTask(@NotNull Runnable runnable);

    public abstract @NotNull Fireworks spawnFireworks(@NotNull Location from);

    public abstract @NotNull CommandManager getCommandManager();

    public @NotNull GameServiceImpl getGameService() {
        return this.gameService;
    }

    public @NotNull MessageService getMessageService() {
        return this.messageService;
    }

    public @NotNull StatsServiceImpl getStatsService() {
        return this.statsService;
    }

    public @Nullable Player getPlayer(@NotNull UUID uuid) {
        return this.getPlayerAdapter().getPlayer(uuid);
    }

    public abstract @NotNull EventAdapter<?> getEventAdapter();

    public abstract @NotNull PlayerAdapter<?, ?> getPlayerAdapter();

    public abstract @NotNull ConfigManager<? extends ThimblePlugin> getConfigManager();

    public abstract @NotNull SavedPlayersManager<?> getSavedPlayersManager();

    public abstract boolean isLoaded(@NotNull Location location);

    public abstract void runSync(@NotNull Runnable runnable);

    public abstract <T> @NotNull CompletableFuture<T> runSync(@NotNull Supplier<T> supplier);

    public @NotNull UpdateChecker getUpdateChecker() {
        return this.updateChecker;
    }

    public @NotNull MainConfig getMainConfig() {
        return this.getConfigManager().getMainConfig();
    }

    protected @NotNull String getPluginFolder() {
        return "plugins/";
    }

    public boolean updatePlugin(@NotNull GitHubAssetInfo assetInfo) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(assetInfo.browserDownloadUrl()).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(this.getPluginFolder() + assetInfo.name())) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            Files.delete(this.getPluginPath());
            return true;
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Unable to download the update.", ex);
            return false;
        }
    }
}
