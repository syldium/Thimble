package me.syldium.thimble.common.config;

import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;

public interface SavedPlayer<P> extends Serializable {

    void save(@NotNull File file);

    void restore(@NotNull P player, boolean restoreInventory, boolean withLocation);

    @SuppressWarnings("unchecked")
    default void restore(@NotNull Player player, boolean restoreInventory, boolean withLocation) {
        this.restore((P) player.getPlugin().getPlayerAdapter().asPlatform(player), restoreInventory, withLocation);
    }
}
