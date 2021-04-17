package me.syldium.thimble.common.util;

import net.kyori.adventure.util.Ticks;

/**
 * A repeating {@link Runnable} that can be cancelled.
 */
public interface Task {

    int GAME_TICKS_PER_SECOND = Ticks.TICKS_PER_SECOND / 2;

    /**
     * Cancel this task.
     */
    void cancel();
}
