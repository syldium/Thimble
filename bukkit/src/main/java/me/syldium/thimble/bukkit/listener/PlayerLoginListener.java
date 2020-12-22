package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.config.SavedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerLoginListener implements Listener {

    private final ThBukkitPlugin plugin;

    public PlayerLoginListener(@NotNull ThBukkitPlugin plugin) {
        this.plugin = plugin;
        if (plugin.getMainConfig().doesSaveStatesInFile()) {
            int savesSize = plugin.getSavedPlayersManager().getSavedUUID().size();
            if (savesSize > 0) {
                plugin.getLogger().info("Found " + savesSize + " player save(s).");
            }
            plugin.registerEvents(this);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (!this.plugin.getSavedPlayersManager().getSavedUUID().contains(uuid)) {
            return;
        }

        this.plugin.getSavedPlayersManager().getInventorySave(uuid).thenAccept(optional ->
            optional.ifPresent(saved -> this.plugin.runSync(() -> this.onPlayerSaveFound(uuid, saved)))
        );
    }

    private void onPlayerSaveFound(@NotNull UUID uuid, @NotNull SavedPlayer<Player> savedPlayer) {
        Player player = this.plugin.getBootstrap().getServer().getPlayer(uuid);
        if (player != null) {
            savedPlayer.restore(player);
            this.plugin.getSavedPlayersManager().delete(uuid);
        }
    }
}
