package me.syldium.thimble.bukkit.world;

import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.PoolBlock;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import static me.syldium.thimble.bukkit.world.BukkitBlockData.IS_FLAT;

public class BukkitPoolBlock implements PoolBlock {

    private final Block handle;

    public BukkitPoolBlock(@NotNull Block handle) {
        this.handle = handle;
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        ((BukkitBlockData) blockData).setBlock(this.handle);
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return IS_FLAT ?
                new BukkitModernBlockData(this.handle.getBlockData())
                : new BukkitMaterialData(this.handle.getType());
    }

    @Override
    public @NotNull BlockVector getPosition() {
        return new BlockVector(this.handle.getX(), this.handle.getY(), this.handle.getZ());
    }

    public @NotNull Block getHandle() {
        return this.handle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        BukkitPoolBlock that = (BukkitPoolBlock) o;
        return this.handle.equals(that.handle);
    }

    @Override
    public int hashCode() {
        return this.handle.hashCode();
    }
}
