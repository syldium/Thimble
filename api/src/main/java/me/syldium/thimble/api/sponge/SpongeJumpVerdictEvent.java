package me.syldium.thimble.api.sponge;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.api.player.ThimblePlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.spongepowered.api.Sponge.getServer;

/**
 * When the result of a jump (or not) is decided.
 *
 * <p>This event is specific to a Sponge environment.</p>
 */
public class SpongeJumpVerdictEvent extends AbstractEvent implements GameEvent {

    private final ThimblePlayer player;
    private JumpVerdict verdict;
    private final Cause cause;

    public SpongeJumpVerdictEvent(@NotNull ThimblePlayer player, @NotNull JumpVerdict verdict, @NotNull Cause cause) {
        this.player = player;
        this.verdict = verdict;
        this.cause = cause;
    }

    @Override
    public @NotNull ThimbleGame game() {
        return this.player.game();
    }

    /**
     * Gets the relevant thimble player.
     *
     * @return The thimble player.
     */
    public @NotNull ThimblePlayer thimblePlayer() {
        return this.player;
    }

    /**
     * Gets the relevant Sponge player if online.
     *
     * @return The Sponge player.
     */
    public @NotNull Optional<@NotNull Player> player() {
        return getServer().getPlayer(this.player.uuid());
    }

    /**
     * Gets the action result.
     *
     * @return The verdict.
     */
    public @NotNull JumpVerdict verdict() {
        return this.verdict;
    }

    /**
     * Sets the action result.
     *
     * @param verdict The verdict.
     */
    public void setVerdict(@NotNull JumpVerdict verdict) {
        this.verdict = requireNonNull(verdict, "verdict");
    }

    @Override
    public @NotNull Cause getCause() {
        return this.cause;
    }

    @Override
    public String toString() {
        return "SpongeJumpVerdictEvent{" +
                "player=" + this.player.name() +
                ", verdict=" + this.verdict +
                '}';
    }
}
