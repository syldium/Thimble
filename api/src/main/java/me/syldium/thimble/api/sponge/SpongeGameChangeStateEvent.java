package me.syldium.thimble.api.sponge;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * When the state of a game changes.
 *
 * <p>This event is not fired for the end of a game, see {@link SpongeGameEndEvent}.</p>
 * <p>This event is specific to a Sponge environment.</p>
 */
public class SpongeGameChangeStateEvent extends AbstractEvent implements Cancellable, GameEvent {

    private final ThimbleGame game;
    private final ThimbleState newState;
    private boolean cancelled = false;
    private final Cause cause;

    /**
     * Creates a new event.
     *
     * @param game The game.
     * @param newState The new game state.
     * @param cause The cause.
     */
    public SpongeGameChangeStateEvent(@NotNull ThimbleGame game, @NotNull ThimbleState newState, @NotNull Cause cause) {
        this.game = game;
        this.newState = newState;
        this.cause = cause;
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
    public @NotNull Cause getCause() {
        return this.cause;
    }

    @Override
    public String toString() {
        return "SpongeGameChangeStateEvent{" +
                "playersSize=" + this.game.size() +
                ", actualState=" + this.game.state() +
                ", newState=" + this.newState +
                ", cancelled=" + this.cancelled +
                '}';
    }
}
