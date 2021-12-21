package me.syldium.thimble.api.bukkit;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleState;
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
    private final ThimbleState newState;
    private boolean cancelled = false;

    /**
     * Creates a new event.
     *
     * @param game The game.
     * @param newState The new game state.
     */
    public BukkitGameChangeStateEvent(@NotNull ThimbleGame game, @NotNull ThimbleState newState) {
        this.game = game;
        this.newState = newState;
    }

    @Override
    public @NotNull ThimbleGame game() {
        return this.game;
    }

    /**
     * Gets the state that will be set if the event is not cancelled.
     *
     * @return The new state.
     */
    public @NotNull ThimbleState newState() {
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

    /**
     * A list of static handlers.
     *
     * @return Static handlers.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public String toString() {
        return "BukkitGameChangeStateEvent{" +
                "playersSize=" + this.game.size() +
                ", actualState=" + this.game.state() +
                ", newState=" + this.newState +
                ", cancelled=" + this.cancelled +
                '}';
    }
}
