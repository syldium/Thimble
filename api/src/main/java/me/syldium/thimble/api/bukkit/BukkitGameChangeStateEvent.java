package me.syldium.thimble.api.bukkit;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleGameState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * When the state of a game changes.
 *
 * <p>This event is not fired for the end of a game, see {@link BukkitGameEndEvent}.</p>
 * <p>This event is specific to a Bukkit environment.</p>
 */
public class BukkitGameChangeStateEvent extends Event implements Cancellable, GameEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ThimbleGame game;
    private final ThimbleGameState newState;
    private boolean cancelled = false;

    public BukkitGameChangeStateEvent(@NotNull ThimbleGame game, @NotNull ThimbleGameState newState) {
        this.game = game;
        this.newState = newState;
    }

    @Override
    public @NotNull ThimbleGame getGame() {
        return this.game;
    }

    public @NotNull ThimbleGameState getNewState() {
        return this.newState;
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
