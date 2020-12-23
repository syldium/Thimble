package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.listener.MoveListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BukkitMoveListener extends MoveListener<ThBukkitPlugin> implements Listener {

    public BukkitMoveListener(@NotNull ThBukkitPlugin plugin) {
        super(plugin);
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (event.getCause() == TeleportCause.COMMAND) {
            this.removePlayerFromArena(uuid);
        } else if (this.quitOnTp) {
            this.removePlayerFromArena(uuid, this.plugin.getPlayerAdapter().asAbstractLocation(event.getTo()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (this.maxDistanceSquared < 1) return;
        BlockVector from = this.plugin.getPlayerAdapter().asBlockVector(event.getFrom());
        BlockVector to = this.plugin.getPlayerAdapter().asBlockVector(event.getTo());
        this.onMove(event.getPlayer().getUniqueId(), from, to);
    }
}
