package me.syldium.thimble.api.arena;

public enum ThimbleGameState {

    WAITING,
    STARTING,
    PLAYING,
    END;

    public boolean acceptPlayers() {
        return this == WAITING || this == STARTING;
    }
}
