package me.syldium.thimble.common.listener;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.ConfigNode;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class MoveListener<P extends ThimblePlugin> {

    protected final P plugin;
    protected final int maxDistanceSquared;
    protected final boolean quitOnTp;

    public MoveListener(@NotNull P plugin) {
        this.plugin = plugin;
        ConfigNode node = plugin.getMainConfig().getGameNode();
        int maxDistance = node.getInt("max-spectator-distance", 40);
        this.maxDistanceSquared = maxDistance > 0 ? maxDistance * maxDistance : -1;
        this.quitOnTp = node.getBool("leave-arena-when-tp", true);
    }

    protected final void onMove(@NotNull UUID playerUniqueId, @NotNull BlockVector from, @NotNull BlockVector to) {
        if (from.equals(to)) return;
        Optional<ThimblePlayer> inGamePlayerOpt = this.plugin.getGameService().getInGamePlayer(playerUniqueId);
        if (!inGamePlayerOpt.isPresent()) return;

        ThimblePlayer inGamePlayer = inGamePlayerOpt.get();
        if (inGamePlayer.isVanished() || inGamePlayer.isJumping()) return;

        Location loc = inGamePlayer.getGame().getState().isStarted() ?
                inGamePlayer.getGame().getArena().getWaitLocation()
                : inGamePlayer.getGame().getArena().getSpawnLocation();
        if (loc == null) return;

        int distanceSquared = (int) loc.horizontalDistanceSquared(to);
        if (distanceSquared > this.maxDistanceSquared) {
            Player player = this.plugin.getPlayer(playerUniqueId);
            if (player != null) {
                player.teleport(loc);
            }
        }
    }

    protected final void removePlayerFromArena(@NotNull UUID playerUniqueId) {
        Optional<ThimbleGame> game = this.getGame(playerUniqueId);
        game.ifPresent(thimbleGame -> thimbleGame.removePlayer(playerUniqueId));
    }

    protected final void removePlayerFromArena(@NotNull UUID playerUniqueId, @NotNull Location location) {
        Optional<ThimbleGame> game = this.getGame(playerUniqueId);
        if (!game.isPresent()) return;

        ThimbleArena arena = game.get().getArena();
        for (Location arenaLoc : new Location[]{arena.getSpawnLocation(), arena.getJumpLocation(), arena.getWaitLocation()}) {
            if ((int) arenaLoc.getX() == (int) location.getX()
                && (int) arenaLoc.getY() == (int) location.getY()
                && (int) arenaLoc.getZ() == (int) location.getZ()) {
                return;
            }
        }
        game.get().removePlayer(playerUniqueId);
    }

    private @NotNull Optional<@NotNull ThimbleGame> getGame(@NotNull UUID playerUniqueId) {
        return this.plugin.getGameService().getGame(playerUniqueId);
    }
}
