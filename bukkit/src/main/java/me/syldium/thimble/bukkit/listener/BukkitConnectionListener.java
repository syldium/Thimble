package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.config.SavedPlayer;
import me.syldium.thimble.common.listener.ConnectionListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BukkitConnectionListener extends ConnectionListener<ThBukkitPlugin, Player> implements Listener {

    public BukkitConnectionListener(@NotNull ThBukkitPlugin plugin) {
        super(plugin);
        plugin.registerEvents(this);
    }

    @Override
    protected void onSavedPlayerFound(@NotNull UUID playerUniqueId, @NotNull SavedPlayer<Player> savedPlayer) {
        Player player = this.plugin.getBootstrap().getServer().getPlayer(playerUniqueId);
        if (player != null) {
            savedPlayer.restore(player);
            this.plugin.getSavedPlayersManager().delete(playerUniqueId);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.onJoin(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.onQuit(event.getPlayer().getUniqueId());
    }
}
