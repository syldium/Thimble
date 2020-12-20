package me.syldium.thimble.common.config;

import me.syldium.thimble.common.ThimblePlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

public abstract class ConfigManager<P extends ThimblePlugin> {

    protected final P plugin;
    private final MainConfig config;

    public ConfigManager(@NotNull P plugin) {
        this.plugin = plugin;
        this.config = new MainConfig(this.getConfig("config"));
    }

    protected abstract @NotNull ConfigFile getConfig(@NotNull File file);

    protected abstract @NotNull String getFileExtension();

    protected void severe(@NotNull String message, @NotNull Throwable throwable) {
        this.plugin.getLogger().log(Level.SEVERE, message, throwable);
    }

    public @NotNull ArenaConfig getArenaConfig() {
        return new ArenaConfig(this.plugin, this.getConfig("arenas"));
    }

    public @NotNull MainConfig getMainConfig() {
        return this.config;
    }

    private @NotNull ConfigFile getConfig(@NotNull String filename) {
        File file = this.plugin.getFile(filename + "." + this.getFileExtension());
        return this.getConfig(file);
    }
}
