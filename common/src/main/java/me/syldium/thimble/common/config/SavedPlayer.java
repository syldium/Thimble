package me.syldium.thimble.common.config;

import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;

public interface SavedPlayer<P> extends Serializable {

    void save(@NotNull File file);

    void restore(@NotNull P player);

    @SuppressWarnings("unchecked")
    default void restore(@NotNull Player player) {
        this.restore((P) player.getPlugin().getPlayerAdapter().asPlatform(player));
    }
}
