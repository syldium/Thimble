package me.syldium.thimble.bukkit.world;

import me.syldium.thimble.common.world.BlockData;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.syldium.thimble.common.ThimblePlugin.classExists;

@SuppressWarnings("deprecation")
public interface BukkitBlockData extends BlockData {

    boolean IS_FLAT = classExists("org.bukkit.block.data.BlockData");

    @NotNull Material material();

    @NotNull ItemStack itemStack();

    void setBlock(@NotNull Block block);

    boolean isSimilar(@NotNull ItemStack itemStack);

    static @NotNull BukkitBlockData build(@NotNull Material material) {
        return IS_FLAT ? new BukkitModernBlockData(material) : new BukkitMaterialData(material);
    }

    static @NotNull BukkitBlockData build(@NotNull ItemStack itemStack) {
        if (IS_FLAT) {
            return new BukkitModernBlockData(itemStack.getType());
        }
        MaterialData data = itemStack.getData();
        return data == null ? new BukkitMaterialData(itemStack.getType()) : new BukkitMaterialData(data);
    }

    static @NotNull List<BukkitBlockData> buildAll(@NotNull Material material) {
        if (IS_FLAT) {
            return Collections.singletonList(new BukkitModernBlockData(material));
        }

        List<BukkitBlockData> blocks = new ArrayList<>();
        Class<? extends MaterialData> dataType = material.getData();
        if (Wool.class.isAssignableFrom(dataType)) {
            for (DyeColor dyeColor : DyeColor.values()) {
                blocks.add(new BukkitMaterialData(new Wool(dyeColor)));
            }
        } else {
            blocks.add(new BukkitMaterialData(material));
        }
        return blocks;
    }
}
