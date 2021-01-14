package me.syldium.thimble.bukkit.world;

import me.syldium.thimble.common.world.BlockData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.syldium.thimble.common.ThimblePlugin.classExists;

public interface BukkitBlockData extends BlockData {

    boolean IS_FLAT = classExists("org.bukkit.block.data.BlockData");

    @NotNull Material material();

    @NotNull ItemStack itemStack();

    void setBlock(@NotNull Block block);

    static @NotNull BukkitBlockData build(@NotNull Material material) {
        return IS_FLAT ? new BukkitModernBlockData(material) : new BukkitMaterialData(material);
    }
}
