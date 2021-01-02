package me.syldium.thimble.api.arena;

/**
 * The different states of a game.
 */
public enum ThimbleState {

    /**
     * Waiting for players.
     */
    WAITING,

    /**
     * The game starts, the countdown has started.
     */
    STARTING,

    /**
     * Players jump.
     */
    PLAYING,

    /**
     * The game ends.
     */
    END;

    /**
     * Tests if the game has started.
     *
     * @return {@code true} if the game has started.
     */
    public boolean isStarted() {
        return this == PLAYING || this == END;
    }

    /**
     * Tests if the game has not started.
     *
     * @return {@code true} if the game has not started or starts.
     */
    public boolean isNotStarted() {
        return this == WAITING || this == STARTING;
    }
}
