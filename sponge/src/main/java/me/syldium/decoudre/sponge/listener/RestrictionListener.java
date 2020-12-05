package me.syldium.decoudre.sponge.listener;

import me.syldium.decoudre.sponge.DeSpongePlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;

public class RestrictionListener {

    private final DeSpongePlugin plugin;

    public RestrictionListener(@NotNull DeSpongePlugin plugin) {
        this.plugin = plugin;
        plugin.registerListeners(this);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent event, @First Player player) {
        if (this.isRestricted(player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onItemDrop(DropItemEvent event, @First Player player) {
        if (this.isRestricted(player)) {
            event.setCancelled(true);
        }
    }

    private boolean isRestricted(@NotNull Player player) {
        return this.plugin.getGameService().getGame(player.getUniqueId()).isPresent()
                && !player.gameMode().get().equals(GameModes.CREATIVE);
    }
}
