package me.syldium.thimble.api.sponge;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * When a game ends.
 *
 * <p>This event is specific to a Sponge environment.</p>
 */
public class SpongeGameEndEvent extends AbstractEvent implements GameEvent {

    private final ThimbleGame game;
    private final ThimblePlayer latest;
    private final boolean solo;
    private final Cause cause;

    public SpongeGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer latest, boolean solo, @NotNull Cause cause) {
        this.game = game;
        this.latest = latest;
        this.solo = solo;
        this.cause = cause;
    }

    @Override
    public @NotNull ThimbleGame game() {
        return this.game;
    }

    /**
     * Returns the player who wins the game.
     *
     * @return The latest player, if any.
     */
    public @Nullable ThimblePlayer getLatestPlayer() {
        return this.latest;
    }

    /**
     * If it's a solo game as per the plugin configuration.
     *
     * @return {@code true} if it's considered as a solo game.
     * @since 1.1.0
     */
    public boolean isSolo() {
        return this.solo;
    }

    @Override
    public @NotNull Cause getCause() {
        return this.cause;
    }
}
