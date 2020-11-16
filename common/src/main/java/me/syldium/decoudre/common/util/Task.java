package me.syldium.decoudre.common.util;

/**
 * A repeating {@link Runnable} that can be cancelled.
 */
public interface Task {

    /**
     * Cancel this task.
     */
    void cancel();
}
