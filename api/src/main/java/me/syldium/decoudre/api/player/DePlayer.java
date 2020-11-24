package me.syldium.decoudre.api.player;

import org.jetbrains.annotations.Range;

public interface DePlayer extends DePlayerStats {

    /**
     * Gets the number of remaining lives.
     *
     * @return A number.
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getLifes();

    /**
     * Returns {@code true} if the player is a game spectator.
     *
     * <p>The spectator status is defined at the beginning of the game and does not change during the game.</p>
     *
     * @return If so.
     */
    boolean isSpectator();
}
