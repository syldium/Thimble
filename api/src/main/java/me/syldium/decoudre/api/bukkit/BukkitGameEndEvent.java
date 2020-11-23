package me.syldium.decoudre.api.bukkit;

import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.player.DePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * When a game ends.
 *
 * <p>This event is specific to a Bukkit environment.</p>
 */
public class BukkitGameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final DeGame game;
    private final DePlayer latest;

    public BukkitGameEndEvent(@NotNull DeGame game, @Nullable DePlayer latest) {
        this.game = game;
        this.latest = latest;
    }

    public @NotNull DeGame getGame() {
        return this.game;
    }

    public @Nullable DePlayer getLatestPlayer() {
        return this.latest;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
