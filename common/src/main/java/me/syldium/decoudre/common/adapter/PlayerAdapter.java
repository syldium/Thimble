package me.syldium.decoudre.common.adapter;

import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.common.player.AbstractPlayer;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Platform specific code.
 *
 * @param <P> Player type
 * @param <L> Location type
 */
public interface PlayerAdapter<P, L> {

    boolean isDeCoudre(@NotNull PoolBlock abstracted);

    @NotNull BlockData getRandomWool();

    @SuppressWarnings("unchecked")
    default @NotNull P asPlatform(@NotNull Player player) {
        return ((AbstractPlayer<P>) player).getHandle();
    }

    @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull UUID worldUUID, @NotNull BlockVector minimumPoint, @NotNull BlockVector maximumPoint);

    @NotNull Player asAbstractPlayer(@NotNull P player);

    @NotNull L asPlatform(@NotNull Location location);

    @NotNull Location asAbstractLocation(@NotNull L location);

    default void openBlockSelectionInventory(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
        this.openBlockSelectionInventory(this.asPlatform(player), inGamePlayer);
    }

    void openBlockSelectionInventory(@NotNull P player, @NotNull InGamePlayer inGamePlayer);
}
