package me.syldium.thimble.api.bukkit;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * When a game ends.
 *
 * <p>This event is specific to a Bukkit environment.</p>
 */
public class BukkitGameEndEvent extends Event implements GameEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ThimbleGame game;
    private final ThimblePlayer latest;
    private final boolean solo;

    /**
     * Creates a new event.
     *
     * @param game The game.
     * @param latest The last player, if any.
     * @param solo If it was a solo game.
     */
    public BukkitGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer latest, boolean solo) {
        this.game = game;
        this.latest = latest;
        this.solo = solo;
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
    public @Nullable ThimblePlayer latestPlayer() {
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
}
