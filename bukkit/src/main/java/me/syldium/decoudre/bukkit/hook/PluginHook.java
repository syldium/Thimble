package me.syldium.decoudre.bukkit.hook;

import me.syldium.decoudre.bukkit.DeBukkitPlugin;
import me.syldium.decoudre.bukkit.DeCoudreBootstrap;
import me.syldium.decoudre.common.config.MainConfig;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Activates hooks if a supported plugin is enabled.
 *
 * <p>The hooks are implemented in the {@code :bukkit:integration} submodule.</p>
 */
public final class PluginHook {

    private final DeBukkitPlugin plugin;
    private final DeCoudreBootstrap bootstrap;
    private final List<String> integrations;

    public PluginHook(@NotNull DeBukkitPlugin plugin, @NotNull DeCoudreBootstrap bootstrap) {
        this.plugin = plugin;
        this.bootstrap = bootstrap;

        MainConfig config = plugin.getMainConfig();
        this.integrations = config.getEnabledIntegrations();
        if (this.isEnabled("PlaceholderAPI")) {
            new DeCoudreExpansion(plugin.getStatsService());
        }
    }

    private boolean isEnabled(@NotNull String pluginName) {
        if (!this.integrations.contains(pluginName)) {
            return false;
        }

        Plugin plugin = this.bootstrap.getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }
}
