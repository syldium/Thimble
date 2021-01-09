package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.api.bukkit.BukkitAdapter;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
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

import java.util.HashSet;
import java.util.Set;

public class RestrictionListener implements Listener {

    private final ThBukkitPlugin plugin;
    private final Set<Material> clickable;
    private final Set<String> allowedCommands;

    public RestrictionListener(@NotNull ThBukkitPlugin plugin, @NotNull Set<@NotNull Material> clickable) {
        this.plugin = plugin;
        this.clickable = clickable;
        this.allowedCommands = new HashSet<>(plugin.getConfig().getStringList("allowed-commands-in-game"));
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
            String[] arguments = this.plugin.getCommandManager().getArgumentsArray(event.getMessage(), -1);
            String label = arguments[0].charAt(0) == '/' ? arguments[0].substring(1) : arguments[0];
            if (!this.allowedCommands.contains(label)) {
                event.setCancelled(true);
                this.plugin.sendFeedback(event.getPlayer(), CommandResult.error(MessageKey.FEEDBACK_GAME_COMMAND));
            }
        }
    }

    private boolean isRestricted(@NotNull Player player) {
        return this.plugin.getGameService().getGame(player.getUniqueId()).isPresent()
                && player.getGameMode() != GameMode.CREATIVE
                && !player.hasPermission("thimble.restrictions.bypass");
    }
}
