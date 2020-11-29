package me.syldium.decoudre.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DeCoudreBootstrap extends JavaPlugin {

    private DeBukkitPlugin plugin;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.plugin = new DeBukkitPlugin(this);
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    public @NotNull DeBukkitPlugin getPlugin() {
        return this.plugin;
    }
}
