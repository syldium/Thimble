package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DamageListener implements Listener {

    private final ThBukkitPlugin plugin;

    public DamageListener(@NotNull ThBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Optional<ThimbleGame> optional = this.plugin.getGameService().getGame(player.getUniqueId());
        if (!optional.isPresent()) return;
        event.setCancelled(true);
        ThimbleGame game = optional.get();

        if (game.isJumping(player.getUniqueId()) && game.verdict(player.getUniqueId(), JumpVerdict.MISSED)) {
            event.setCancelled(true);
            player.setVelocity(new Vector(0, 0, 0));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!this.plugin.getGameService().getGame(event.getEntity().getUniqueId()).isPresent()) return;

        Player player = (Player) event.getEntity();
        if (player.getFoodLevel() > event.getFoodLevel()) {
            event.setCancelled(true);
        }
    }
}
