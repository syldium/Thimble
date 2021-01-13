package me.syldium.thimble.bukkit.hook;

import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.bukkit.ThBootstrap;
import me.syldium.thimble.common.command.arena.ArenaCommand;
import me.syldium.thimble.common.command.migrate.MigrateCommand;
import me.syldium.thimble.common.config.MainConfig;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Activates hooks if a supported plugin is enabled.
 *
 * <p>The hooks are implemented in the {@code :bukkit:integration} submodule.</p>
 */
public final class PluginHook {

    private final ThBukkitPlugin plugin;
    private final ThBootstrap bootstrap;
    private final List<String> integrations;

    public PluginHook(@NotNull ThBukkitPlugin plugin, @NotNull ThBootstrap bootstrap) {
        this.plugin = plugin;
        this.bootstrap = bootstrap;

        MainConfig config = plugin.getMainConfig();
        this.integrations = config.getEnabledIntegrations();
        if (this.isEnabled("LuckPerms")) {
            new LuckPermsContext(bootstrap.getServer().getServicesManager(), plugin.getGameService());
        }
        if (this.isEnabled("PlaceholderAPI")) {
            new ThimbleExpansion(plugin.getStatsService());
        }
        if (this.isEnabled("Parties")) {
            new PartiesArenaListener(bootstrap);
        }
        if (this.isEnabled("WorldEdit")) {
            plugin.getCommandManager().lookup(ArenaCommand.class).getChildren()
                    .add(new RegionCommand(bootstrap.getServer()));
        }
        File file = new File(plugin.getDataFolder().getParentFile(), "DeACoudre");
        if (file.isDirectory()) {
            plugin.getCommandManager().lookup(MigrateCommand.class).getChildren()
                    .add(new DeACoudreMigration(this.getPlugin("DeACoudre"), file));
        }
    }

    private boolean isEnabled(@NotNull String pluginName) {
        if (!this.integrations.contains(pluginName)) {
            return false;
        }

        Plugin plugin = this.getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    private @Nullable Plugin getPlugin(@NotNull String pluginName) {
        return this.bootstrap.getServer().getPluginManager().getPlugin(pluginName);
    }
}
