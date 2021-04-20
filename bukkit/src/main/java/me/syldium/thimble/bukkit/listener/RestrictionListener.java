package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.api.bukkit.BukkitAdapter;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.util.StringUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RestrictionListener implements Listener {

    private final ThBukkitPlugin plugin;
    private final Set<Material> clickable;

    public RestrictionListener(@NotNull ThBukkitPlugin plugin, @NotNull Set<@NotNull Material> clickable) {
        this.plugin = plugin;
        this.clickable = clickable;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isRestricted(event.getPlayer())) {
            event.setCancelled(true);
        }
        if (this.clickable.contains(event.getBlock().getType())) {
            Block block = event.getBlock();
            this.plugin.getGameService().removeSign(BukkitAdapter.get().asAbstract(block));
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

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (this.isRestricted(event.getPlayer())) {
            String literal = event.getMessage();
            if (literal.isEmpty()) return;

            String command = literal.charAt(0) == '/' ? literal.substring(1) : literal;
            String label = StringUtil.firstToken(command, ' ');
            if (!this.plugin.getMainConfig().getAllowedCommands().contains(label)) {
                event.setCancelled(true);
                this.plugin.sendFeedback(event.getPlayer(), CommandResult.error(MessageKey.FEEDBACK_GAME_COMMAND));
            }
        }
    }

    private boolean isRestricted(@NotNull Player player) {
        return this.plugin.getGameService().playerGame(player.getUniqueId()).isPresent()
                && player.getGameMode() != GameMode.CREATIVE
                && !player.hasPermission("thimble.restrictions.bypass");
    }
}
