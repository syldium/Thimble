package me.syldium.thimble.sponge.adapter;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.adapter.EventAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class SpongeEventAdapter implements EventAdapter<ServerPlayer> {
    @Override
    public boolean callPlayerJoinArenaEvent(@NotNull ThimbleGame game, @NotNull ServerPlayer player) {
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
}
