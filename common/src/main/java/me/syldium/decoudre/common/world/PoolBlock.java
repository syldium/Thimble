package me.syldium.decoudre.common.world;

import org.jetbrains.annotations.NotNull;

public interface PoolBlock {

    void setBlockData(@NotNull BlockData blockData);

    @NotNull BlockData getBlockData();

    boolean isPassable();
}
