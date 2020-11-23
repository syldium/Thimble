package me.syldium.decoudre.common.adapter;

import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.common.player.AbstractPlayer;
import me.syldium.decoudre.common.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EventAdapter<P> {

    boolean callPlayerJoinArenaEvent(@NotNull DeGame game, @NotNull P player);

    void callGameEndEvent(@NotNull DeGame game, @Nullable DePlayer player);

    @SuppressWarnings("unchecked")
    default boolean callPlayerJoinArenaEvent(@NotNull DeGame arena, @NotNull Player player) {
        return this.callPlayerJoinArenaEvent(arena, ((AbstractPlayer<P>) player).getHandle());
    }
}
