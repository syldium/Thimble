package me.syldium.thimble.common.listener;

import me.syldium.thimble.common.config.ConfigManager;
import org.jetbrains.annotations.NotNull;

/**
 * Designates an element that should be updated when the configuration is reloaded.
 */
public interface Reloadable {

    /**
     * Update.
     *
     * @param configManager The config manager after being reloaded.
     */
    void reload(@NotNull ConfigManager<?> configManager);
}
