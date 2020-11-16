package me.syldium.decoudre.bukkit.config;

import me.syldium.decoudre.bukkit.DeBukkitPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public abstract class FileConfig {

    protected final DeBukkitPlugin plugin;
    private final File file;
    protected final FileConfiguration configuration;

    public FileConfig(@NotNull DeBukkitPlugin plugin, @NotNull File file) {
        this.plugin = plugin;
        this.file = file;
        this.configuration = new YamlConfiguration();
        try {
            this.configuration.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to load the config file!", ex);
        }
    }

    protected @NotNull String getString(@NotNull String path, @Nullable String def) {
        // noinspection ConstantConditions
        return this.configuration.getString(path, def);
    }

    protected @Nullable String getString(@NotNull String path) {
        return this.configuration.getString(path);
    }

    protected void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.file, ex);
        }
    }
}
