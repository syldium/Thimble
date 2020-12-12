package me.syldium.thimble.sponge.listener;

import com.flowpowered.math.vector.Vector3d;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.sponge.ThSpongePlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

import java.util.Optional;

public class DamageListener {

    private final ThSpongePlugin plugin;

    public DamageListener(@NotNull ThSpongePlugin plugin) {
        this.plugin = plugin;
        plugin.registerListeners(this);
    }

    @Listener
    public void onPlayerDamage(DamageEntityEvent event, @First Player player) {
        Optional<ThimbleGame> optional = this.plugin.getGameService().getGame(player.getUniqueId());
        if (!optional.isPresent()) return;
        event.setCancelled(true);
        ThimbleGame game = optional.get();

        if (game.isJumping(player.getUniqueId()) && game.verdict(player.getUniqueId(), JumpVerdict.MISSED)) {
            event.setCancelled(true);
            player.setVelocity(Vector3d.ZERO);
        }
    }

    @Listener
    public void onFoodLevelChange(ChangeDataHolderEvent.ValueChange event, @First Player player) {
        for (ImmutableValue<?> immutableValue : event.getOriginalChanges().getSuccessfulData()) {
            if (immutableValue.getKey().equals(Keys.FOOD_LEVEL)) {
                if (!this.plugin.getGameService().getGame(player.getUniqueId()).isPresent()) {
                    return;
                }
                event.setCancelled(true);
            }
        }
    }
}
