package me.syldium.thimble.api.util;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Defines a block position in a world.
 */
public class BlockPos extends BlockVector {

    private final Key worldKey;

    public BlockPos(@NotNull Key worldKey, int x, int y, int z) {
        super(x, y, z);
        this.worldKey = worldKey;
    }

    public BlockPos(@NotNull BlockVector vector, @NotNull Key worldKey) {
        super(vector.x(), vector.y(), vector.z());
        this.worldKey = requireNonNull(worldKey, "world resource key");
    }

    /**
     * Gets the resource key of the world.
     *
     * @return The {@link Key}.
     */
    public @NotNull Key worldKey() {
        return this.worldKey;
    }

    /**
     * Computes the squared distance with another {@link BlockPos}.
     *
     * @param other A block position.
     * @return The squared distance.
     * @throws IllegalArgumentException If the worlds are different.
     */
    public int distanceSquared(@NotNull BlockPos other) {
        if (!Objects.equals(this.worldKey, other.worldKey)) {
            throw new IllegalArgumentException("Cannot determine the center between two different worlds!");
        }
        return super.distanceSquared(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BlockPos blockPos = (BlockPos) o;
        return this.worldKey.equals(blockPos.worldKey);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 53 * this.worldKey.hashCode();
    }

    @Override
    public String toString() {
        return "BlockPos{" + this.x + "," + this.y + "," + this.z + "," + this.worldKey.asString() + "}";
    }
}
