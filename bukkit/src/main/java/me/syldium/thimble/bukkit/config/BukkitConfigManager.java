package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.config.ConfigFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BukkitConfigManager extends ConfigManager<ThBukkitPlugin> {

    public BukkitConfigManager(@NotNull ThBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull ConfigFile getConfig(@NotNull File file) {
        if ("config.yml".equals(file.getName())) {
            return new BukkitConfigFile(file, this.plugin.getBootstrap().getConfig());
        }
        return new BukkitConfigFile(file, YamlConfiguration.loadConfiguration(file));
    }

    @Override
    protected final @NotNull String getFileExtension() {
        return "yml";
    }

    @Override
    public void reload() {
        this.plugin.getBootstrap().reloadConfig();
        super.reload();
    }
}
