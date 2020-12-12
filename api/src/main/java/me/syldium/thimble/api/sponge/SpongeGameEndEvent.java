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
    private final Cause cause;

    public SpongeGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer latest, @NotNull Cause cause) {
        this.game = game;
        this.latest = latest;
        this.cause = cause;
    }

    @Override
    public @NotNull ThimbleGame getGame() {
        return this.game;
    }

    public @Nullable ThimblePlayer getLatestPlayer() {
        return this.latest;
    }

    @Override
    public @NotNull Cause getCause() {
        return this.cause;
    }
}
