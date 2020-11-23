package me.syldium.decoudre.api.sponge;

import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.player.DePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * When a game ends.
 *
 * <p>This event is specific to a Sponge environment.</p>
 */
public class SpongeGameEndEvent extends AbstractEvent {

    private final DeGame game;
    private final DePlayer latest;
    private final Cause cause;

    public SpongeGameEndEvent(@NotNull DeGame game, @Nullable DePlayer latest, @NotNull Cause cause) {
        this.game = game;
        this.latest = latest;
        this.cause = cause;
    }

    public @NotNull DeGame getGame() {
        return this.game;
    }

    public @Nullable DePlayer getLatestPlayer() {
        return this.latest;
    }

    @Override
    public @NotNull Cause getCause() {
        return this.cause;
    }
}
