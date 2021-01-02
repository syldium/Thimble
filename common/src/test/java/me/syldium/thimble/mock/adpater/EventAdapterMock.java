package me.syldium.thimble.mock.adpater;

import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleGameState;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.adapter.EventAdapter;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EventAdapterMock implements EventAdapter<PlayerMock> {

    @Override
    public boolean callPlayerJoinArenaEvent(@NotNull ThimbleGame game, @NotNull PlayerMock player) {
        return false;
    }

    @Override
    public boolean callGameChangeState(@NotNull ThimbleGame game, @NotNull ThimbleGameState newState) {
        return false;
    }

    @Override
    public void callGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer player) {

    }

    @Override
    public boolean callPlayerJoinArenaEvent(@NotNull ThimbleGame arena, @NotNull Player player) {
        return false;
    }
}
