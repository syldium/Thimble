package me.syldium.thimble.api.sponge;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * When a player is about to join an arena.
 *
 * <p>This event is specific to a Sponge environment.</p>
 */
public class SpongePlayerJoinArenaEvent extends AbstractEvent implements TargetPlayerEvent, Cancellable, GameEvent {

    private final ThimbleGame game;
    private final Player player;
    private boolean cancelled = false;
    private final Cause cause;

    public SpongePlayerJoinArenaEvent(@NotNull ThimbleGame game, @NotNull Player player, @NotNull Cause cause) {
        this.game = game;
        this.player = player;
        this.cause = cause;
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
    public @NotNull Player getTargetEntity() {
        return this.player;
    }

    @Override
    public @NotNull Cause getCause() {
        return this.cause;
    }
}
