package me.syldium.thimble.sponge.world;

import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.Blocks;
import me.syldium.thimble.common.world.PoolBlock;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SpongePoolBlock implements PoolBlock {

    private final BlockState handle;
    private final Location<World> location;

    public SpongePoolBlock(@NotNull BlockState handle, @NotNull Location<@NotNull World> location) {
        this.handle = handle;
        this.location = location;
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        if (Blocks.WATER.equals(blockData)) {
            this.location.setBlockType(BlockTypes.WATER);
            return;
        }
        this.location.setBlock(((SpongeBlockData) blockData).handle);
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return new SpongeBlockData(this.handle);
    }

    @Override
    public @NotNull BlockVector getPosition() {
        return new BlockVector(this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());
    }

    public @NotNull Location<@NotNull World> getLocation() {
        return this.location;
    }
}
