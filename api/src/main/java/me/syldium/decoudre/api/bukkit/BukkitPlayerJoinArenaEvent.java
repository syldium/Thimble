package me.syldium.decoudre.api.bukkit;

import me.syldium.decoudre.api.arena.DeArena;
import me.syldium.decoudre.api.arena.DeGame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * When a player is about to join an arena.
 *
 * <p>This event is specific to a Bukkit environment.</p>
 */
public class BukkitPlayerJoinArenaEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final DeGame game;
    private boolean cancelled = false;

    public BukkitPlayerJoinArenaEvent(@NotNull DeGame game, @NotNull Player who) {
        super(who);
        this.game = game;
    }

    public @NotNull DeArena getArena() {
        return this.game.getArena();
    }

    public @NotNull DeGame getGame() {
        return this.game;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
