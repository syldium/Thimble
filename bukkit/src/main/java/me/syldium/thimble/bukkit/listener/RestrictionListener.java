package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.bukkit.ThBukkitPlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

public class RestrictionListener implements Listener {

    private final ThBukkitPlugin plugin;

    public RestrictionListener(@NotNull ThBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isRestricted(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isRestricted(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (this.isRestricted(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    private boolean isRestricted(@NotNull Player player) {
        return this.plugin.getGameService().getGame(player.getUniqueId()).isPresent()
                && player.getGameMode() != GameMode.CREATIVE;
    }
}
