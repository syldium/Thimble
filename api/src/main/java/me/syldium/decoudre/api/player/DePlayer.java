package me.syldium.decoudre.api.player;

import me.syldium.decoudre.api.arena.DeGame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface DePlayer extends DePlayerStats {

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
    @NotNull DeGame getGame();
}
