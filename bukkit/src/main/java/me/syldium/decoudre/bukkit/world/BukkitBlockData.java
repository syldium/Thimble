package me.syldium.decoudre.bukkit.world;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class BukkitBlockData implements me.syldium.decoudre.common.world.BlockData {

    final BlockData handle;

    public BukkitBlockData(@NotNull BlockData handle) {
        this.handle = handle;
    }

    public @NotNull BlockData getHandle() {
        return this.handle;
    }
}
