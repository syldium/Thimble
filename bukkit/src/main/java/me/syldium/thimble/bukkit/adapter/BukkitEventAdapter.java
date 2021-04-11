package me.syldium.thimble.bukkit.adapter;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.bukkit.BukkitGameAbortedEvent;
import me.syldium.thimble.api.bukkit.BukkitGameChangeStateEvent;
import me.syldium.thimble.api.bukkit.BukkitGameEndEvent;
import me.syldium.thimble.api.bukkit.BukkitPlayerJoinArenaEvent;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.adapter.EventAdapter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitEventAdapter implements EventAdapter<Player> {

    private final PluginManager pluginManager;

    public BukkitEventAdapter(@NotNull PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public boolean callPlayerJoinArenaEvent(@NotNull ThimbleGame game, @NotNull Player player) {
        BukkitPlayerJoinArenaEvent event = new BukkitPlayerJoinArenaEvent(game, player);
        this.pluginManager.callEvent(event);
        return event.isCancelled();
    }

    @Override
    public boolean callGameChangeState(@NotNull ThimbleGame game, @NotNull ThimbleState newState) {
        BukkitGameChangeStateEvent event = new BukkitGameChangeStateEvent(game, newState);
        this.pluginManager.callEvent(event);
        return event.isCancelled();
    }

    @Override
    public void callGameEndEvent(@NotNull ThimbleGame game, @Nullable ThimblePlayer player, boolean isSolo) {
        BukkitGameEndEvent event = new BukkitGameEndEvent(game, player, isSolo);
        this.pluginManager.callEvent(event);
    }

    @Override
    public void callGameAbortedEvent(@NotNull ThimbleGame game, boolean startAborted, boolean willBeEmpty) {
        BukkitGameAbortedEvent event = new BukkitGameAbortedEvent(game, startAborted, willBeEmpty);
        this.pluginManager.callEvent(event);
    }
}
