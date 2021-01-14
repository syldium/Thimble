package me.syldium.thimble.api.arena;

import me.syldium.thimble.api.player.JumpVerdict;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A game mode where players jump one by one.
 */
public interface ThimbleSingleGame extends ThimbleGame {

    /**
     * Defines the jump result of the current jumper.
     *
     * @param verdict The jump result.
     * @return If all went well.
     * @throws IllegalStateException If no players are currently jumping.
     */
    default boolean verdict(@NotNull JumpVerdict verdict) {
        if (this.currentJumper() == null) {
            throw new IllegalStateException("No players is currently jumping.");
        }
        return this.verdict(this.currentJumper(), verdict);
    }

    /**
     * Gets the player to whom it is the turn to jump.
     *
     * @return The jumper, if any.
     */
    @Contract(pure = true)
    @Nullable UUID currentJumper();

    /**
     * Returns the player who will jump right after.
     *
     * @return The jumper, if any.
     */
    @Contract(pure = true)
    @Nullable UUID peekNextJumper();
}
