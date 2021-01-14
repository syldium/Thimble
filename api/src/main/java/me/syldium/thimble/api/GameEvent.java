package me.syldium.thimble.api;

import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import org.jetbrains.annotations.NotNull;

/**
 * An event during a game.
 */
public interface GameEvent {

    /**
     * Gets the game where the event was fired.
     *
     * @return The game.
     */
    @NotNull ThimbleGame getGame();

    /**
     * Gets the arena where the event was fired.
     *
     * @return The arena.
     */
    default @NotNull ThimbleArena getArena() {
        return this.getGame().arena();
    }
}
