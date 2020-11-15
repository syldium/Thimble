package me.syldium.decoudre.api.player;

import org.jetbrains.annotations.Range;

public interface DePlayer extends DePlayerStats {

    /**
     * Get the number of remaining lives.
     *
     * @return A number
     */
    @Range(from=0, to=Integer.MAX_VALUE) int getLifes();
}
