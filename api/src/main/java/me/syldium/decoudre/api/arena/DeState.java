package me.syldium.decoudre.api.arena;

public enum DeState {

    WAITING,
    STARTING,
    PLAYING,
    END;

    public boolean acceptPlayers() {
        return this == WAITING || this == STARTING;
    }
}
