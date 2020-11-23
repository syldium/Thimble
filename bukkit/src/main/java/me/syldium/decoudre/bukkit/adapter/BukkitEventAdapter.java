package me.syldium.decoudre.bukkit.adapter;

import me.syldium.decoudre.api.arena.DeGame;
import me.syldium.decoudre.api.bukkit.BukkitGameEndEvent;
import me.syldium.decoudre.api.bukkit.BukkitPlayerJoinArenaEvent;
import me.syldium.decoudre.api.player.DePlayer;
import me.syldium.decoudre.common.adapter.EventAdapter;
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
    public boolean callPlayerJoinArenaEvent(@NotNull DeGame game, @NotNull Player player) {
        BukkitPlayerJoinArenaEvent event = new BukkitPlayerJoinArenaEvent(game, player);
        this.pluginManager.callEvent(event);
        return event.isCancelled();
    }

    @Override
    public void callGameEndEvent(@NotNull DeGame game, @Nullable DePlayer player) {
        BukkitGameEndEvent event = new BukkitGameEndEvent(game, player);
        this.pluginManager.callEvent(event);
    }
}
