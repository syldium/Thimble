package me.syldium.decoudre.common.world;

import me.syldium.decoudre.api.BlockVector;
import org.jetbrains.annotations.NotNull;

public interface PoolBlock {

    void setBlockData(@NotNull BlockData blockData);

    @NotNull BlockData getBlockData();

    @NotNull BlockVector getPosition();
}
