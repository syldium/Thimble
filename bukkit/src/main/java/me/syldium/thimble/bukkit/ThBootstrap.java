package me.syldium.thimble.bukkit;

import me.syldium.thimble.api.bukkit.BukkitAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ThBootstrap extends JavaPlugin {

    private static final int PLUGIN_ID = 9881;

    private ThBukkitPlugin plugin;

    @Override
    public void onEnable() {
        if (!me.syldium.thimble.bukkit.world.BukkitBlockData.IS_FLAT) {
            this.getLogger().warning("This plugin is not actively tested with older versions.");
        }

        this.saveDefaultConfig();
        this.plugin = new ThBukkitPlugin(this);

        try {
            Metrics metrics = new Metrics(this, PLUGIN_ID);
            metrics.addCustomChart(new Metrics.SimplePie("arena_count", this.plugin.getGameService()::arenaCount));
            metrics.addCustomChart(new Metrics.SimplePie("locale_used", () -> this.plugin.getMainConfig().getLocale().toLanguageTag()));
        } catch (ExceptionInInitializerError ex) {
            this.getLogger().severe(ex.getCause().getMessage());
        }

        if (this.getConfig().getBoolean("update-checker", true)) {
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.plugin.getUpdateChecker(), 5L, 20L * 60L * 60L * 24L);
        }
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
        this.plugin = null;
        BukkitAdapter.unregister();
    }

    @Override
    public @NotNull File getFile() {
        return super.getFile();
    }

    public @NotNull ThBukkitPlugin getPlugin() {
        return this.plugin;
    }
}
