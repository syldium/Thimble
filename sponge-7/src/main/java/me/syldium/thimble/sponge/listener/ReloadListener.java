package me.syldium.thimble.sponge.listener;

import me.syldium.thimble.sponge.ThSpongePlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;

public class ReloadListener {

    private final ThSpongePlugin plugin;

    public ReloadListener(@NotNull ThSpongePlugin plugin) {
        this.plugin = plugin;
        plugin.registerListeners(this);
    }

    @Listener
    public void onGameReload(GameReloadEvent event) {
        this.plugin.getConfigManager().reload();
    }
}
