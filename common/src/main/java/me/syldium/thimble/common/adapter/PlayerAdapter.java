package me.syldium.thimble.common.adapter;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.media.Scoreboard;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
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

    @NotNull List<? extends BlockData> getAvailableBlocks();

    @Nullable BlockData getThimbleBlock();

    void clearPool(@NotNull WorldKey worldKey, @NotNull Map<BlockVector, BlockData> blocks);

    default @NotNull BlockData getRandomBlock() {
        final List<? extends BlockData> availableBlocks = this.getAvailableBlocks();
        if (availableBlocks.isEmpty()) {
            throw new IllegalStateException("No available blocks to choose from");
        }
        return availableBlocks.get(new Random().nextInt(availableBlocks.size()));
    }

    @SuppressWarnings("unchecked")
    default @NotNull P asPlatform(@NotNull Player player) {
        return ((AbstractPlayer<P>) player).getHandle();
    }

    @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull WorldKey worldKey, @NotNull BlockVector minimumPoint, @NotNull BlockVector maximumPoint);

    @Nullable Player getPlayer(@NotNull UUID uuid);

    @Nullable Player getPlayer(@NotNull String name);

    @NotNull Player asAbstractPlayer(@NotNull P player);

    @NotNull L asPlatform(@NotNull Location location);

    @NotNull Location asAbstractLocation(@NotNull L location);

    default void openBlockSelectionInventory(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
        this.openBlockSelectionInventory(this.asPlatform(player), inGamePlayer);
    }

    void openBlockSelectionInventory(@NotNull P player, @NotNull InGamePlayer inGamePlayer);

    void setScoreboard(@Nullable Scoreboard scoreboard, @NotNull Player player);

    void hideScoreboard(@NotNull Scoreboard scoreboard, @NotNull Player player);
}
