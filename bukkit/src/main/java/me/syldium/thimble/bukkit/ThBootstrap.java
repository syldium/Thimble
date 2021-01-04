package me.syldium.thimble.bukkit;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class ThBootstrap extends JavaPlugin {

    private static final int PLUGIN_ID = 9881;

    private ThBukkitPlugin plugin;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.plugin = new ThBukkitPlugin(this);

        try {
            Metrics metrics = new Metrics(this, PLUGIN_ID);
            metrics.addCustomChart(new Metrics.SimplePie("arena_count", this.plugin.getGameService()::getArenaCount));
            metrics.addCustomChart(new Metrics.SingleLineChart("average_capacity", this.plugin.getGameService()::getAveragePlayerCapacity));
        } catch (ExceptionInInitializerError ex) {
            this.getLogger().severe(ex.getCause().getMessage());
        }
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    public @NotNull ThBukkitPlugin getPlugin() {
        return this.plugin;
    }
}
