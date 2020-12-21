package me.syldium.thimble.api.arena;

/**
 * The different states of a game.
 */
public enum ThimbleGameState {

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

    public boolean acceptPlayers() {
        return this == WAITING || this == STARTING;
    }
}
