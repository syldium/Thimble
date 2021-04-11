package me.syldium.thimble.api.bukkit;

import me.syldium.thimble.api.GameEvent;
import me.syldium.thimble.api.arena.ThimbleGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * When a game start is interrupted or when all players leave the arena.
 *
 * <p>This event is specific to a Bukkit environment.</p>
 *
 * @since 1.1.2
 */
public class BukkitGameAbortedEvent extends Event implements GameEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ThimbleGame game;
    private final boolean startAborted;
    private final boolean willBeEmpty;

    /**
     * Creates a new event.
     *
     * @param game The part where the event happens.
     * @param startAborted {@code true} if a countdown has been interrupted.
     * @param willBeEmpty {@code true} if the game is or will be empty.
     */
    public BukkitGameAbortedEvent(@NotNull ThimbleGame game, boolean startAborted, boolean willBeEmpty) {
        this.game = game;
        this.startAborted = startAborted;
        this.willBeEmpty = willBeEmpty;
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
        return "BukkitGameAbortedEvent{" +
                "gameState=" + this.game.state() +
                ", playersSize=" + this.game.size() +
                ", startAborted=" + this.startAborted +
                ", willBeEmpty=" + this.willBeEmpty +
                '}';
    }
}
