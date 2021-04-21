package me.syldium.thimble.sponge.listener;

import me.syldium.thimble.common.config.SavedPlayer;
import me.syldium.thimble.common.listener.ConnectionListener;
import me.syldium.thimble.sponge.ThSpongePlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.Optional;
import java.util.UUID;

public class SpongeConnectionListener extends ConnectionListener<ThSpongePlugin, Player> {

    public SpongeConnectionListener(@NotNull ThSpongePlugin plugin) {
        super(plugin);
        plugin.registerListeners(this);
    }

    @Override
    protected void onSavedPlayerFound(@NotNull UUID playerUniqueId, @NotNull SavedPlayer<Player> savedPlayer) {
        Optional<Player> playerOpt = this.plugin.getServer().getPlayer(playerUniqueId);
        if (playerOpt.isPresent()) {
            savedPlayer.restore(playerOpt.get(), this.inventoryCleared, true);
            this.plugin.getSavedPlayersManager().delete(playerUniqueId);
        }
    }

    @Listener
    public void onAuth(ClientConnectionEvent.Auth event) {
        this.onPreLogin(event.getProfile().getUniqueId());
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event, @First Player player) {
        this.onJoin(player.getUniqueId());
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect event, @First Player player) {
        UUID uuid = player.getUniqueId();
        this.onQuit(uuid);
        this.plugin.getPlayerAdapter().unregisterPlayer(uuid);
    }
}
