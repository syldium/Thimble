package me.syldium.thimble.common;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.Thimble;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.dependency.Dependency;
import me.syldium.thimble.common.dependency.DependencyInjection;
import me.syldium.thimble.common.dependency.DependencyResolver;
import me.syldium.thimble.common.listener.Reloadable;
import me.syldium.thimble.common.service.ExternalPlaceholderProvider;
import me.syldium.thimble.common.service.ScoreboardHolderService;
import me.syldium.thimble.common.service.ScoreboardService;
import me.syldium.thimble.common.service.SqlDataService;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.syldium.thimble.common.player.ThimblePlaceholder.TAG_END;
import static me.syldium.thimble.common.player.ThimblePlaceholder.TAG_START;

public abstract class ThimblePlugin {

    protected static final String WINNER_TAG = TAG_START + "winner" + TAG_END;

    private final Executor dbExecutor = Executors.newSingleThreadExecutor(task -> new Thread(task, "Thimble-db"));
    protected final Collection<Reloadable> reloadables = new LinkedList<>();

    private GameServiceImpl gameService;
    private MessageService messageService;
    private ScoreboardService scoreboardService;
    private StatsServiceImpl statsService;
    private UpdateChecker updateChecker;

    public void enable() {
        if (!classExists("com.google.gson.JsonElement")) {
            Path gson = new DependencyResolver(this).downloadDependency(Dependency.GSON);
            DependencyInjection.addJarToClasspath(gson, this);
        }
        this.gameService = new GameServiceImpl(this);
        this.messageService = new MessageServiceImpl(this);
        this.loadServices();
        this.updateChecker = new UpdateChecker(Thimble.pluginVersion(), this.getServerType(), this.getLogger());
        this.gameService.load();
        Thimble.setGameService(this.gameService);
        Thimble.setStatsService(this.statsService);
    }

    public void disable() {
        Thimble.setGameService(null);
        Thimble.setStatsService(null);
        this.gameService.save();
        this.statsService.close();
        this.reloadables.clear();
    }

    public void reload() {
        for (Reloadable reloadable : this.reloadables) {
            reloadable.reload(this.getConfigManager());
        }
    }

    public @NotNull File getFile(@NotNull String filename, boolean createIfNotExist) {
        File file = new File(this.getDataFolder(), filename);
        try {
            if (createIfNotExist && !file.exists()) {
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

    protected @NotNull ScoreboardService constructScoreboardService() {
        return new ScoreboardHolderService(this);
    }

    protected @NotNull StatsServiceImpl constructStatsService() {
        MainConfig config = this.getMainConfig();
        SqlDataService dataService = DataService.fromConfig(this, config);
        return new StatsServiceImpl(dataService, this.dbExecutor, config.getCacheNode());
    }

    public void loadServices() {
        if (this.statsService != null) {
            this.statsService.close();
        }
        this.statsService = this.constructStatsService();
        this.scoreboardService = this.constructScoreboardService();
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

    public @NotNull ScoreboardService getScoreboardService() {
        return this.scoreboardService;
    }

    public @NotNull StatsServiceImpl getStatsService() {
        return this.statsService;
    }

    public @NotNull ExternalPlaceholderProvider placeholderProvider() {
        return new ExternalPlaceholderProvider(this::getStatsService, this::getGameService);
    }

    public @Nullable Player getPlayer(@NotNull UUID uuid) {
        return this.getPlayerAdapter().getPlayer(uuid);
    }

    public @Nullable Player getPlayer(@NotNull String name) {
        return this.getPlayerAdapter().getPlayer(name);
    }

    public abstract @NotNull String getPlayerName(@NotNull UUID uuid);

    public abstract void executeGameEndCommands(@NotNull ThimblePlayer winner);

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
