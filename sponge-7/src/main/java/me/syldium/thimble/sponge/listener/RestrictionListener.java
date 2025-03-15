package me.syldium.thimble.sponge.listener;

import me.syldium.thimble.sponge.ThSpongePlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;

public class RestrictionListener {

    private final ThSpongePlugin plugin;

    public RestrictionListener(@NotNull ThSpongePlugin plugin) {
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
        return this.plugin.getGameService().playerGame(player.getUniqueId()).isPresent()
                && !player.gameMode().get().equals(GameModes.CREATIVE);
    }
}
