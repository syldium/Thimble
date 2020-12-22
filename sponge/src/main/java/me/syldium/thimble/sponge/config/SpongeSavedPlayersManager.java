package me.syldium.thimble.sponge.config;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.SavedPlayersManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.Player;

import java.io.File;
import java.io.IOException;

public class SpongeSavedPlayersManager extends SavedPlayersManager<Player> {

    public SpongeSavedPlayersManager(@NotNull ThimblePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @Nullable SpongeSavedPlayer load(@NotNull File file) {
        try {
            return new SpongeSavedPlayer(file);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected @NotNull SpongeSavedPlayer create(@NotNull Player player) {
        return new SpongeSavedPlayer(player);
    }
}
