package me.syldium.thimble.bukkit;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.bukkit.BukkitAdapter;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.bukkit.command.PaperCommand;
import me.syldium.thimble.bukkit.config.BlockConfig;
import me.syldium.thimble.common.util.ServerType;
import me.syldium.thimble.bukkit.adapter.BukkitEventAdapter;
import me.syldium.thimble.bukkit.adapter.BukkitPlayerAdapter;
import me.syldium.thimble.bukkit.command.BukkitCommandExecutor;
import me.syldium.thimble.bukkit.command.PaperAsyncRegisteredCommand;
import me.syldium.thimble.bukkit.config.BukkitConfigManager;
import me.syldium.thimble.bukkit.config.BukkitSavedPlayersManager;
import me.syldium.thimble.bukkit.hook.PluginHook;
import me.syldium.thimble.bukkit.listener.BukkitMoveListener;
import me.syldium.thimble.bukkit.listener.DamageListener;
import me.syldium.thimble.bukkit.listener.BukkitConnectionListener;
import me.syldium.thimble.bukkit.listener.SignInteractListener;
import me.syldium.thimble.bukkit.listener.RestrictionListener;
import me.syldium.thimble.bukkit.listener.SignChangeListener;
import me.syldium.thimble.bukkit.util.BukkitFireworks;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.util.Fireworks;
import me.syldium.thimble.common.util.Task;
import me.syldium.thimble.bukkit.util.BukkitTask;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static me.syldium.thimble.bukkit.util.BukkitUtil.setServerVersion;

public class ThBukkitPlugin extends ThimblePlugin {

    private final ThBootstrap bootstrap;
    private final BukkitAudiences audiences;
    private final BukkitCommandExecutor commandExecutor;
    private final BukkitEventAdapter eventAdapter;
    private final BukkitPlayerAdapter playerAdapter;
    private final BukkitConfigManager configManager;
    private final BukkitSavedPlayersManager savedPlayersManager;
    private final PluginHook hooks;

    public ThBukkitPlugin(@NotNull ThBootstrap bootstrap) {
        setServerVersion();
        this.bootstrap = bootstrap;
        this.audiences = BukkitAudiences.create(bootstrap);
        this.configManager = new BukkitConfigManager(this);
        this.enable();

        this.eventAdapter = new BukkitEventAdapter(bootstrap.getServer().getPluginManager());
        this.playerAdapter = new BukkitPlayerAdapter(this, bootstrap, this.audiences);
        this.savedPlayersManager = new BukkitSavedPlayersManager(this);

        ServicesManager servicesManager = bootstrap.getServer().getServicesManager();
        servicesManager.register(GameService.class, this.getGameService(), bootstrap, ServicePriority.High);
        servicesManager.register(StatsService.class, this.getStatsService(), bootstrap, ServicePriority.High);

        PluginCommand command = Objects.requireNonNull(bootstrap.getCommand("thimble"), "Command not registered");
        List<String> aliases = bootstrap.getConfig().isList("aliases") ?
                bootstrap.getConfig().getStringList("aliases")
                : Collections.singletonList("th");
        if (classExists("io.papermc.paper.command.brigadier.Commands")) {
            this.commandExecutor = new PaperCommand(this, command, aliases);
        } else if (classExists("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent")) {
            this.commandExecutor = new PaperAsyncRegisteredCommand<>(this, command, aliases);
        } else {
            this.commandExecutor = new BukkitCommandExecutor(this, command, aliases);
        }

        BlockConfig reloadable = new BlockConfig(this);
        this.reloadables.add(reloadable);
        new DamageListener(this);
        new SignInteractListener(this, reloadable.clickable());
        this.reloadables.add(new SignChangeListener(this, reloadable.clickable()));
        new RestrictionListener(this, reloadable.clickable());
        this.reloadables.add(new BukkitConnectionListener(this));
        this.reloadables.add(new BukkitMoveListener(this));
        this.reloadables.add(this.playerAdapter);
        this.hooks = new PluginHook(this, bootstrap);
        this.getMessageService().setExternalPlaceholderService(this.hooks);
    }

    @Override
    public void disable() {
        super.disable();
        this.audiences.close();
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.bootstrap.getLogger();
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.bootstrap.getDataFolder();
    }

    @Override
    public @NotNull Path getPluginPath() {
        return this.bootstrap.getFile().toPath().toAbsolutePath();
    }

    @Override
    public @NotNull ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    public @NotNull Task startGameTask(@NotNull Runnable runnable) {
        return new BukkitTask(this.getServer().getScheduler().scheduleSyncRepeatingTask(this.bootstrap, runnable, 0L, 2L));
    }

    @Override
    public @NotNull Fireworks spawnFireworks(@NotNull Location from) {
        return new BukkitFireworks(this.playerAdapter.asPlatform(from));
    }

    @Override
    public @NotNull BukkitCommandExecutor getCommandManager() {
        return this.commandExecutor;
    }

    @Override
    public @NotNull String getPlayerName(@NotNull UUID uuid) {
        String name = this.getServer().getOfflinePlayer(uuid).getName();
        return name == null ? uuid.toString() : name;
    }

    @Override
    public void executeGameEndCommands(@NotNull ThimblePlayer winner) {
        for (String command : this.getConfig().getStringList("commands-at-end")) {
            this.getServer().dispatchCommand(this.getServer().getConsoleSender(), command.replace(WINNER_TAG, winner.name()));
        }
    }

    public void sendFeedback(@NotNull org.bukkit.entity.Player bukkitPlayer, @NotNull CommandResult result) {
        this.playerAdapter.asAbstractPlayer(bukkitPlayer).sendFeedback(result);
    }

    @Override
    public @NotNull BukkitEventAdapter getEventAdapter() {
        return this.eventAdapter;
    }

    @Override
    public @NotNull BukkitPlayerAdapter getPlayerAdapter() {
        return this.playerAdapter;
    }

    @Override
    public @NotNull BukkitConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public @NotNull BukkitSavedPlayersManager getSavedPlayersManager() {
        return this.savedPlayersManager;
    }

    @Override
    public boolean isLoaded(@NotNull Location location) {
        return BukkitAdapter.get().worldFromKey(location.worldKey()) != null;
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        this.getServer().getScheduler().runTask(this.bootstrap, runnable);
    }

    @Override
    public @NotNull <T> CompletableFuture<T> runSync(@NotNull Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        this.getServer().getScheduler().runTask(this.bootstrap, () -> future.complete(supplier.get()));
        return future;
    }

    public void registerEvents(@NotNull Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this.bootstrap);
    }

    public @NotNull ThBootstrap getBootstrap() {
        return this.bootstrap;
    }

    public @NotNull FileConfiguration getConfig() {
        return this.bootstrap.getConfig();
    }

    public @NotNull Server getServer() {
        return this.bootstrap.getServer();
    }
}
