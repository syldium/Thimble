package me.syldium.thimble.common.world;

import me.syldium.thimble.api.BlockVector;
import org.jetbrains.annotations.NotNull;

public interface PoolBlock {

    void setBlockData(@NotNull BlockData blockData);

    @NotNull BlockData getBlockData();

    @NotNull BlockVector getPosition();
}
