package me.syldium.thimble.api;

import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGame;
import org.jetbrains.annotations.NotNull;

public interface GameEvent {

    @NotNull ThimbleGame getGame();

    default @NotNull ThimbleArena getArena() {
        return this.getGame().getArena();
    }
}
