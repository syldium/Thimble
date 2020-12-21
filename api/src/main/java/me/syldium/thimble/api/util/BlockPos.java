package me.syldium.thimble.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Defines a block position in a world.
 */
public class BlockPos extends BlockVector {

    private final UUID world;

    public BlockPos(@NotNull UUID world, int x, int y, int z) {
        super(x, y, z);
        this.world = world;
    }

    public BlockPos(@NotNull UUID world, @NotNull BlockVector vector) {
        super(vector.getX(), vector.getY(), vector.getZ());
        this.world = world;
    }

    /**
     * Gets the unique identifier of the world.
     *
     * @return The {@link UUID}.
     */
    public @NotNull UUID getWorldUUID() {
        return this.world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BlockPos blockPos = (BlockPos) o;
        return this.world.equals(blockPos.world);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 53 * this.world.hashCode();
    }

    @Override
    public String toString() {
        return "BlockPos{" + this.x + "," + this.y + "," + this.z + "," + this.world + "}";
    }
}
