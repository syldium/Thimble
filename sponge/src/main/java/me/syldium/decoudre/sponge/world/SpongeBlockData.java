package me.syldium.decoudre.sponge.world;

import me.syldium.decoudre.common.world.BlockData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;

public class SpongeBlockData implements BlockData {

    final BlockState handle;

    public SpongeBlockData(@NotNull BlockState blockState) {
        this.handle = blockState;
    }
}
