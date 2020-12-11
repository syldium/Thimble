package me.syldium.decoudre.bukkit.listener;

import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.player.JumpVerdict;
import me.syldium.decoudre.bukkit.DeBukkitPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DamageListener implements Listener {

    private final DeBukkitPlugin plugin;

    public DamageListener(@NotNull DeBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Optional<DeGame> optional = this.plugin.getGameService().getGame(player.getUniqueId());
        if (!optional.isPresent()) return;
        event.setCancelled(true);
        DeGame game = optional.get();

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
