package me.syldium.thimble.api.bukkit;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.api.player.ThimblePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Bukkit.getPlayer;

/**
 * When the result of a jump (or not) is decided.
 *
 * <p>This event is specific to a Bukkit environment.</p>
 *
 * @since 1.2.0
 */
public class BukkitJumpVerdictEvent extends Event implements GameEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ThimblePlayer player;
    private JumpVerdict verdict;

    /**
     * Creates a new event.
     *
     * @param player The player.
     * @param verdict The "normal" result.
     */
    public BukkitJumpVerdictEvent(@NotNull ThimblePlayer player, @NotNull JumpVerdict verdict) {
        this.player = player;
        this.verdict = verdict;
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
     * Gets the relevant Bukkit player if online.
     *
     * @return The Bukkit player.
     */
    public @Nullable Player player() {
        return getPlayer(this.player.uuid());
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
        return "BukkitJumpVerdictEvent{" +
                "player=" + this.player.name() +
                ", verdict=" + this.verdict +
                '}';
    }
}
