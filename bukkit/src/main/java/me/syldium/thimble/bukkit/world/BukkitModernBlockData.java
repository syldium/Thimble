package me.syldium.thimble.bukkit.world;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class BukkitModernBlockData implements BukkitBlockData {

    final BlockData handle;

    BukkitModernBlockData(@NotNull Material material) {
        this.handle = material.createBlockData();
    }

    BukkitModernBlockData(@NotNull BlockData handle) {
        this.handle = handle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitModernBlockData that = (BukkitModernBlockData) o;
        return this.handle.getMaterial().equals(that.handle.getMaterial());
    }

    @Override
    public int hashCode() {
        return this.handle.getMaterial().hashCode();
    }

    @Override
    public @NotNull Material material() {
        return this.handle.getMaterial();
    }

    @Override
    public @NotNull ItemStack itemStack() {
        return new ItemStack(this.handle.getMaterial());
    }

    @Override
    public void setBlock(@NotNull Block block) {
        block.setBlockData(this.handle);
    }
}
