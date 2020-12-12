package me.syldium.thimble.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class ThBootstrap extends JavaPlugin {

    private ThBukkitPlugin plugin;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.plugin = new ThBukkitPlugin(this);
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    public @NotNull ThBukkitPlugin getPlugin() {
        return this.plugin;
    }
}
