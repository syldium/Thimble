package me.syldium.thimble.bukkit.world;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class BukkitBlockData implements me.syldium.thimble.common.world.BlockData {

    final BlockData handle;

    public BukkitBlockData(@NotNull Material material) {
        this.handle = material.createBlockData();
    }

    public BukkitBlockData(@NotNull BlockData handle) {
        this.handle = handle;
    }

    public @NotNull BlockData getHandle() {
        return this.handle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitBlockData that = (BukkitBlockData) o;
        return this.handle.getMaterial().equals(that.handle.getMaterial());
    }

    @Override
    public int hashCode() {
        return this.handle.getMaterial().hashCode();
    }
}
