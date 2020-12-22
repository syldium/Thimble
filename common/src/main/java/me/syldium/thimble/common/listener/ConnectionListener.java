package me.syldium.thimble.common.listener;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.SavedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public abstract class ConnectionListener<Plugin extends ThimblePlugin, Player> {

    protected final Plugin plugin;

    public ConnectionListener(@NotNull Plugin plugin) {
        this.plugin = plugin;
        int savesSize = plugin.getSavedPlayersManager().getPending().size();
        if (savesSize > 0) {
            plugin.getLogger().info("Found " + savesSize + " player save(s).");
        }
    }

    @SuppressWarnings("unchecked")
    public final void onJoin(@NotNull UUID playerUniqueId) {
        if (!this.plugin.getSavedPlayersManager().getPending().contains(playerUniqueId)) {
            return;
        }

        this.plugin.getSavedPlayersManager().getInventorySave(playerUniqueId).thenAccept(optional ->
                optional.ifPresent(saved -> this.plugin.runSync(() -> this.onSavedPlayerFound(playerUniqueId, (SavedPlayer<Player>) saved)))
        );
    }

    public final void onQuit(@NotNull UUID playerUniqueId) {
        Optional<ThimbleGame> optional = this.plugin.getGameService().getGame(playerUniqueId);
        if (!optional.isPresent()) return;
        ThimbleGame game = optional.get();

        if (game.getState().acceptPlayers()) {
            game.removePlayer(playerUniqueId);
        } else {
            this.plugin.getSavedPlayersManager().getPending().add(playerUniqueId);
        }
    }

    protected abstract void onSavedPlayerFound(@NotNull UUID playerUniqueId, @NotNull SavedPlayer<Player> savedPlayer);
}
