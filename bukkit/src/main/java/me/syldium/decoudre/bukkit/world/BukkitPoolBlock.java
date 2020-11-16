package me.syldium.decoudre.bukkit.world;

import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.Blocks;
import me.syldium.decoudre.common.world.PoolBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class BukkitPoolBlock implements PoolBlock {

    private final Block handle;

    public BukkitPoolBlock(@NotNull Block handle) {
        this.handle = handle;
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        if (Blocks.WATER.equals(blockData)) {
            this.handle.setType(Material.WATER);
            return;
        }
        this.handle.setBlockData(((BukkitBlockData) blockData).handle);
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return new BukkitBlockData(this.handle.getBlockData());
    }

    @Override
    public boolean isPassable() {
        return this.handle.isPassable();
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
