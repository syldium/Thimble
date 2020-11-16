package me.syldium.decoudre.common.adapter;

import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;

/**
 * Platform specific code.
 *
 * @param <P> Player type
 * @param <L> Location type
 */
public abstract class PlayerAdapter<P, L> {

    public abstract boolean isDeCoudre(@NotNull PoolBlock abstracted);

    public abstract @NotNull BlockData getRandomWool();

    public abstract @NotNull P asPlatform(@NotNull Player player);

    public abstract @NotNull Player asAbstractPlayer(@NotNull P player);

    public abstract @NotNull L asPlatform(@NotNull Location location);

    public abstract @NotNull Location asAbstractLocation(@NotNull L location);
}
