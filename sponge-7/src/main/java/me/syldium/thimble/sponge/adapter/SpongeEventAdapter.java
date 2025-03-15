package me.syldium.thimble.sponge.adapter;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.player.JumpVerdict;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.sponge.SpongeGameAbortedEvent;
import me.syldium.thimble.api.sponge.SpongeGameChangeStateEvent;
import me.syldium.thimble.api.sponge.SpongeGameEndEvent;
import me.syldium.thimble.api.sponge.SpongeJumpVerdictEvent;
import me.syldium.thimble.api.sponge.SpongePlayerJoinArenaEvent;
import me.syldium.thimble.common.adapter.EventAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;

public class SpongeEventAdapter implements EventAdapter<Player> {

    private final PluginContainer container;
    private final EventContext eventContext;

    public SpongeEventAdapter(@NotNull PluginContainer container) {
        this.container = container;
        this.eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, this.container).build();
    }

    @Override
    public void callGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer player, boolean isSolo) {
        SpongeGameEndEvent event = new SpongeGameEndEvent(game, player, isSolo, Cause.of(this.eventContext, this.container));
        Sponge.getEventManager().post(event);
    }

    @Override
    public void callGameAbortedEvent(@NotNull ThimbleGame game, boolean startAborted, boolean willBeEmpty) {
        SpongeGameAbortedEvent event = new SpongeGameAbortedEvent(game, startAborted, willBeEmpty, Cause.of(this.eventContext, this.container));
        Sponge.getEventManager().post(event);
    }

    @Override
    public boolean callPlayerJoinArenaEvent(@NotNull ThimbleGame game, @NotNull Player player) {
        EventContext context = EventContext.builder()
                .add(EventContextKeys.PLUGIN, this.container)
                .add(EventContextKeys.PLAYER, player)
                .build();
        SpongePlayerJoinArenaEvent event = new SpongePlayerJoinArenaEvent(game, player, Cause.of(context, this.container));
        Sponge.getEventManager().post(event);
        return event.isCancelled();
    }

    @Override
    public boolean callGameChangeState(@NotNull ThimbleGame game, @NotNull ThimbleState newState) {
        SpongeGameChangeStateEvent event = new SpongeGameChangeStateEvent(game, newState, Cause.of(this.eventContext, this.container));
        Sponge.getEventManager().post(event);
        return event.isCancelled();
    }

    @Override
    public @NotNull JumpVerdict callJumpVerdictEvent(@NotNull ThimblePlayer player, @NotNull JumpVerdict verdict) {
        SpongeJumpVerdictEvent event = new SpongeJumpVerdictEvent(player, verdict, Cause.of(this.eventContext, this.container));
        Sponge.getEventManager().post(event);
        return event.verdict();
    }
}
