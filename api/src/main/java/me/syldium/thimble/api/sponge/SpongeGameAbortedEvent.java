package me.syldium.thimble.api.sponge;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * When a game start is interrupted or when all players leave the arena.
 *
 * <p>This event is specific to a Sponge environment.</p>
 * 
 * @since 1.1.2
 */
public class SpongeGameAbortedEvent extends AbstractEvent implements GameEvent {

    private final ThimbleGame game;
    private final boolean startAborted;
    private final boolean willBeEmpty;
    private final Cause cause;

    /**
     * Creates a new event.
     *
     * @param game The part where the event happens.
     * @param startAborted {@code true} if a countdown has been interrupted.
     * @param willBeEmpty {@code true} if the game is or will be empty.
     * @param cause The cause.
     */
    public SpongeGameAbortedEvent(@NotNull ThimbleGame game, boolean startAborted, boolean willBeEmpty, @NotNull Cause cause) {
        this.game = game;
        this.startAborted = startAborted;
        this.willBeEmpty = willBeEmpty;
        this.cause = cause;
    }

    @Override
    public @NotNull ThimbleGame game() {
        return this.game;
    }

    /**
     * Returns {@code true} if a timer had been started and then interrupted.
     *
     * @return {@code true} if a countdown has been interrupted.
     */
    public boolean isStartAborted() {
        return this.startAborted;
    }

    /**
     * Returns {@code true} if there are no players left and the game will no longer be referenced.
     *
     * @return {@code true} if the game will be empty.
     */
    public boolean willBeEmpty() {
        return this.willBeEmpty;
    }

    @Override
    public @NotNull Cause getCause() {
        return this.cause;
    }

    @Override
    public String toString() {
        return "SpongeGameAbortedEvent{" +
                "gameState=" + this.game.state() +
                ", playersSize=" + this.game.size() +
                ", startAborted=" + this.startAborted +
                ", willBeEmpty=" + this.willBeEmpty +
                '}';
    }
}
