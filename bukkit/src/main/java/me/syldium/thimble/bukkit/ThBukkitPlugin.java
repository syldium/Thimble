package me.syldium.thimble.bukkit;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.bukkit.adapter.BukkitEventAdapter;
import me.syldium.thimble.bukkit.adapter.BukkitPlayerAdapter;
import me.syldium.thimble.bukkit.command.BukkitCommandExecutor;
import me.syldium.thimble.bukkit.command.PaperCommandExecutor;
import me.syldium.thimble.bukkit.hook.PluginHook;
import me.syldium.thimble.bukkit.listener.DamageListener;
import me.syldium.thimble.bukkit.listener.SignInteractListener;
import me.syldium.thimble.bukkit.listener.RestrictionListener;
import me.syldium.thimble.bukkit.listener.SignChangeListener;
import me.syldium.thimble.bukkit.util.BukkitFireworks;
import me.syldium.thimble.bukkit.util.BukkitUtil;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.util.Fireworks;
import me.syldium.thimble.common.util.Task;
import me.syldium.thimble.bukkit.config.BukkitArenaConfig;
import me.syldium.thimble.bukkit.config.BukkitMainConfig;
import me.syldium.thimble.bukkit.util.BukkitTask;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class ThBukkitPlugin extends ThimblePlugin {

    private final ThBootstrap bootstrap;
    private final BukkitAudiences audiences;
    private final BukkitCommandExecutor commandExecutor;
    private final BukkitEventAdapter eventAdapter;
    private final BukkitPlayerAdapter playerAdapter;
    private final BukkitArenaConfig arenaConfig;

    public ThBukkitPlugin(@NotNull ThBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.audiences = BukkitAudiences.create(bootstrap);
        this.arenaConfig = new BukkitArenaConfig(this, this.getFile("arenas.yml"));
        this.enable(new BukkitMainConfig(this, this.getFile("config.yml")));

        this.eventAdapter = new BukkitEventAdapter(bootstrap.getServer().getPluginManager());
        this.playerAdapter = new BukkitPlayerAdapter(this, bootstrap, this.audiences);

        ServicesManager servicesManager = bootstrap.getServer().getServicesManager();
        servicesManager.register(GameService.class, this.getGameService(), bootstrap, ServicePriority.High);
        servicesManager.register(StatsService.class, this.getStatsService(), bootstrap, ServicePriority.High);

        PluginCommand command = Objects.requireNonNull(bootstrap.getCommand("thimble"), "Command not registered");
        if (classExists("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent")) {
            this.commandExecutor = new PaperCommandExecutor<>(this, command);
        } else {
            this.commandExecutor = new BukkitCommandExecutor(this, command);
        }

        Set<Material> clickable = BukkitUtil.getAllBlocksMatching(this.getLogger(), this.getConfig().getStringList("clickable"));
        new DamageListener(this);
        new SignInteractListener(this, clickable);
        new SignChangeListener(this, clickable);
        new RestrictionListener(this, clickable);
        new PluginHook(this, bootstrap);
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.bootstrap.getLogger();
    }

    @Override
    public @NotNull BukkitArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.bootstrap.getDataFolder();
    }

    @Override
    public @NotNull Task startGameTask(@NotNull Runnable runnable) {
        return new BukkitTask(this.bootstrap.getServer().getScheduler().scheduleSyncRepeatingTask(this.bootstrap, runnable, 0L, 1L));
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
    public @Nullable Player getPlayer(@NotNull UUID uuid) {
        org.bukkit.entity.Player player = this.bootstrap.getServer().getPlayer(uuid);
        if (player == null) {
            return null;
        }
        return this.playerAdapter.asAbstractPlayer(player);
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

    public void registerEvents(@NotNull Listener listener) {
        this.bootstrap.getServer().getPluginManager().registerEvents(listener, this.bootstrap);
    }

    public @NotNull ThBootstrap getBootstrap() {
        return this.bootstrap;
    }

    public @NotNull FileConfiguration getConfig() {
        return this.bootstrap.getConfig();
    }
}
