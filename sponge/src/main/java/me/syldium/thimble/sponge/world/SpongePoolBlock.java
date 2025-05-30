package me.syldium.thimble.sponge.world;

import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.world.server.ServerLocation;

public class SpongePoolBlock implements PoolBlock {

    private final ServerLocation location;

    public SpongePoolBlock(@NotNull ServerLocation location) {
        this.location = location;
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        this.location.setBlock(((SpongeBlockData) blockData).handle);
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return new SpongeBlockData(this.location.block());
    }

    @Override
    public @NotNull BlockVector getPosition() {
        return new BlockVector(this.location.blockX(), this.location.blockY(), this.location.blockZ());
    }
}
