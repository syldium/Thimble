package me.syldium.thimble.common.config;

import me.syldium.thimble.common.ThimblePlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Level;

public abstract class ConfigManager<P extends ThimblePlugin> {

    protected final P plugin;
    private MainConfig config;

    public ConfigManager(@NotNull P plugin) {
        this.plugin = plugin;
        this.load();
    }

    protected abstract @NotNull ConfigFile getConfig(@NotNull File file);

    protected abstract @NotNull String getFileExtension();

    protected void severe(@NotNull String message, @NotNull Throwable throwable) {
        this.plugin.getLogger().log(Level.SEVERE, message, throwable);
    }

    public @NotNull ArenaConfig getArenaConfig() {
        return new ArenaConfig(this.plugin, this.getConfig("arenas"));
    }

    public @Nullable ConfigFile getScoreboardConfig() {
        return this.getConfig("scoreboard", false);
    }

    public @NotNull MainConfig getMainConfig() {
        return this.config;
    }

    private @NotNull ConfigFile getConfig(@NotNull String filename) {
        return this.getConfig(filename, true);
    }

    @Contract("_, true -> !null")
    private @Nullable ConfigFile getConfig(@NotNull String filename, boolean createIfNotExist) {
        File file = this.plugin.getFile(filename + "." + this.getFileExtension(), createIfNotExist);
        if (!file.exists()) {
            return null;
        }
        return this.getConfig(file);
    }

    private void load() {
        this.config = new MainConfig(this.getConfig("config"));
    }

    public void reload() {
        this.plugin.getGameService().save();
        this.load();
        this.plugin.getMessageService().updateLocale();
        this.plugin.reload();
    }
}
