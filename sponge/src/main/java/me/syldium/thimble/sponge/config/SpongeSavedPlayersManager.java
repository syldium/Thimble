package me.syldium.thimble.sponge.config;

import me.syldium.thimble.common.config.SavedPlayersManager;
import me.syldium.thimble.sponge.ThSpongePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SpongeSavedPlayersManager extends SavedPlayersManager<Player> {

    private final ThSpongePlugin plugin;

    public SpongeSavedPlayersManager(@NotNull ThSpongePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    protected @Nullable SpongeSavedPlayer load(@NotNull File file) {
        try (ObjectInputStream data = new ObjectInputStream(new FileInputStream(file))) {
            return (SpongeSavedPlayer) data.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected @NotNull SpongeSavedPlayer create(@NotNull Player player) {
        return new SpongeSavedPlayer(this.plugin.getPlayerAdapter(), player);
    }
}
