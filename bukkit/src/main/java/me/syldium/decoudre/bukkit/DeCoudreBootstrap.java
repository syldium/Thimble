package me.syldium.decoudre.bukkit;

import me.syldium.decoudre.bukkit.command.BukkitCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class DeCoudreBootstrap extends JavaPlugin {

    private DeBukkitPlugin plugin;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.plugin = new DeBukkitPlugin(this);
        new BukkitCommandExecutor(this.plugin, Objects.requireNonNull(this.getCommand("dac")));
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    public @NotNull DeBukkitPlugin getPlugin() {
        return this.plugin;
    }
}
