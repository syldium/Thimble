package me.syldium.thimble.mock.adapter;

import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.mock.player.PlayerMock;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleState;
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
    public boolean callGameChangeState(@NotNull ThimbleGame game, @NotNull ThimbleState newState) {
        return false;
    }

    @Override
    public @NotNull JumpVerdict callJumpVerdictEvent(@NotNull ThimblePlayer player, @NotNull JumpVerdict verdict) {
        return verdict;
    }

    @Override
    public void callGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer player, boolean isSolo) {

    }

    @Override
    public void callGameAbortedEvent(@NotNull ThimbleGame game, boolean startAborted, boolean willBeEmpty) {

    }

    @Override
    public boolean callPlayerJoinArenaEvent(@NotNull ThimbleGame arena, @NotNull Player player) {
        return false;
    }
}
