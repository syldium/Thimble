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

    public BukkitGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer latest) {
        this.game = game;
        this.latest = latest;
    }

    @Override
    public @NotNull ThimbleGame getGame() {
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
