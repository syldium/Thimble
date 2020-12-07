package me.syldium.decoudre.sponge.world;

import me.syldium.decoudre.common.world.BlockData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;

public class SpongeBlockData implements BlockData {

    final BlockState handle;

    public SpongeBlockData(@NotNull BlockState blockState) {
        this.handle = blockState;
    }

    public @NotNull BlockState getHandle() {
        return this.handle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpongeBlockData blockData = (SpongeBlockData) o;
        return this.handle.equals(blockData.handle);
    }

    @Override
    public int hashCode() {
        return this.handle.hashCode();
    }
}
