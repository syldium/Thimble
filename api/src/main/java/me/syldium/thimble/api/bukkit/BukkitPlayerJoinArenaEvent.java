package me.syldium.thimble.api.bukkit;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
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
public class BukkitPlayerJoinArenaEvent extends PlayerEvent implements Cancellable, GameEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ThimbleGame game;
    private boolean cancelled = false;

    /**
     * Creates a new event.
     *
     * @param game The game.
     * @param who The player who will join the arena.
     */
    public BukkitPlayerJoinArenaEvent(@NotNull ThimbleGame game, @NotNull Player who) {
        super(who);
        this.game = game;
    }

    @Override
    public @NotNull ThimbleGame game() {
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

    /**
     * An list of static handlers.
     *
     * @return Static handlers.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public String toString() {
        return "BukkitPlayerJoinArenaEvent{" +
                "gameState=" + this.game.state() +
                ", playersSize=" + this.game.size() +
                ", who=" + this.player +
                ", cancelled=" + this.cancelled +
                '}';
    }
}
