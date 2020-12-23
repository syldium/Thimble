package me.syldium.thimble.sponge.listener;

import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.listener.MoveListener;
import me.syldium.thimble.sponge.ThSpongePlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.teleport.TeleportType;
import org.spongepowered.api.event.cause.entity.teleport.TeleportTypes;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SpongeMoveListener extends MoveListener<ThSpongePlugin> {

    public SpongeMoveListener(@NotNull ThSpongePlugin plugin) {
        super(plugin);
        plugin.registerListeners(this);
    }

    @Listener
    public void onTeleport(MoveEntityEvent.Teleport event, @First Player player, @First TeleportType teleportType) {
        if (teleportType.equals(TeleportTypes.COMMAND)) {
            this.removePlayerFromArena(player.getUniqueId());
        } else if (this.quitOnTp) {
            this.removePlayerFromArena(player.getUniqueId(), this.plugin.getPlayerAdapter().asAbstractLocation(event.getToTransform()));
        }
    }

    @Listener
    public void onMove(MoveEntityEvent.Position event, @First Player player) {
        if (this.maxDistanceSquared < 1) return;
        BlockVector from = this.plugin.getPlayerAdapter().asBlockVector(event.getFromTransform().getPosition());
        BlockVector to = this.plugin.getPlayerAdapter().asBlockVector(event.getToTransform().getPosition());
        this.onMove(player.getUniqueId(), from, to);
    }
}
