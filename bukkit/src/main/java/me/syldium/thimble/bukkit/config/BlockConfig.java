package me.syldium.thimble.bukkit.config;

import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.bukkit.util.BukkitUtil;
import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.listener.Reloadable;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.HashSet;
import java.util.Set;

public class BlockConfig implements Reloadable {

    private final ThBukkitPlugin plugin;
    private final Set<Material> clickable = new HashSet<>();

    public BlockConfig(@NotNull ThBukkitPlugin plugin) {
        this.plugin = plugin;
        this.reload(plugin.getConfigManager());
    }

    @Override
    public void reload(@NotNull ConfigManager<?> configManager) {
        this.clickable.clear();
        this.clickable.addAll(BukkitUtil.getAllBlocksMatching(this.plugin.getLogger(), this.plugin.getConfig().getStringList("clickable")));
    }

    public @NotNull @UnmodifiableView Set<@NotNull Material> clickable() {
        return this.clickable;
    }
}
