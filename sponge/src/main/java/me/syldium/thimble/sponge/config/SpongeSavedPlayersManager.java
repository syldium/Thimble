package me.syldium.thimble.sponge.config;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.config.SavedPlayersManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class SpongeSavedPlayersManager extends SavedPlayersManager<ServerPlayer> {

    public SpongeSavedPlayersManager(@NotNull ThimblePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @Nullable SpongeSavedPlayer load(@NotNull File file) {
        try (InputStream stream = new FileInputStream(file);
             ObjectInputStream data = new ObjectInputStream(stream)) {
            return (SpongeSavedPlayer) data.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected @NotNull SpongeSavedPlayer create(@NotNull ServerPlayer player) {
        return new SpongeSavedPlayer(player);
    }
}
