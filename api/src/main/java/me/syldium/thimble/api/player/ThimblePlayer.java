package me.syldium.thimble.api.player;

import me.syldium.thimble.api.arena.ThimbleGame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface ThimblePlayer extends ThimblePlayerStats {

    /**
     * Gets the number of points - lifes in single mode.
     *
     * @return A number.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getPoints();

    /**
     * Returns {@code true} if the player is a game spectator.
     *
     * <p>The spectator status is defined at the beginning of the game and does not change during the game.</p>
     *
     * @return If so.
     */
    boolean isSpectator();

    /**
     * Gets the game the player is in.
     *
     * @return The game.
     */
    @NotNull ThimbleGame getGame();
}
