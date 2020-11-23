package me.syldium.decoudre.sponge.adapter;

import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.api.sponge.SpongeGameEndEvent;
import me.syldium.decoudre.api.sponge.SpongePlayerJoinArenaEvent;
import me.syldium.decoudre.common.adapter.EventAdapter;
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
    public void callGameEndEvent(@NotNull DeGame game, @Nullable DePlayer player) {
        SpongeGameEndEvent event = new SpongeGameEndEvent(game, player, Cause.of(this.eventContext, this.container));
        Sponge.getEventManager().post(event);
    }

    @Override
    public boolean callPlayerJoinArenaEvent(@NotNull DeGame game, @NotNull Player player) {
        SpongePlayerJoinArenaEvent event = new SpongePlayerJoinArenaEvent(game, player, Cause.of(this.eventContext, this.container));
        Sponge.getEventManager().post(event);
        return event.isCancelled();
    }
}
