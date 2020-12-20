package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.bukkit.ThBootstrap;
import me.syldium.thimble.common.config.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

class BukkitConfigFile extends BukkitConfigNode implements ConfigFile {

    private final File file;

    BukkitConfigFile(@NotNull File file, @NotNull FileConfiguration fileConfiguration) {
        super(fileConfiguration);
        this.file = file;
    }

    @Override
    public void save() {
        try {
            ((FileConfiguration) this.section).save(this.file);
        } catch (IOException ex) {
            ThBootstrap.getPlugin(ThBootstrap.class).getLogger().log(Level.SEVERE, "Could not save config to " + this.file, ex);
        }
    }
}
