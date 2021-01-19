package me.syldium.thimble.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Defines an immutable block position in a world.
 */
public class BlockPos extends BlockVector {

    /** The world's resource key. */
    private final WorldKey worldKey;

    /**
     * Constructs a new block position with the given coordinates.
     *
     * @param worldKey The world's resource key.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     */
    public BlockPos(@NotNull WorldKey worldKey, int x, int y, int z) {
        super(x, y, z);
        this.worldKey = requireNonNull(worldKey, "world resource key");
    }

    /**
     * Constructs a new block position from a block vector and a resource key.
     *
     * @param vector The block vector.
     * @param worldKey The world's resource key.
     */
    public BlockPos(@NotNull BlockVector vector, @NotNull WorldKey worldKey) {
        super(vector.x(), vector.y(), vector.z());
        this.worldKey = requireNonNull(worldKey, "world resource key");
    }

    /**
     * Gets the resource key of the world.
     *
     * @return The {@link WorldKey}.
     */
    public @NotNull WorldKey worldKey() {
        return this.worldKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull BlockPos add(int x, int y, int z) {
        return new BlockPos(this.worldKey, this.x + x, this.y + y, this.z + z);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull BlockPos multiply(int amount) {
        return new BlockPos(this.worldKey, this.x * amount, this.y * amount, this.z * amount);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull BlockPos max(@NotNull BlockVector other) {
        return new BlockPos(this.worldKey, Math.max(this.x, other.x), Math.max(this.y, other.y), Math.max(this.z, other.z));
    }

    /**
     * {@inheritDoc}
     */
    public @NotNull BlockPos min(@NotNull BlockVector other) {
        return new BlockPos(this.worldKey, Math.min(this.x, other.x), Math.min(this.y, other.y), Math.min(this.z, other.z));
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

    @Override
    public @NotNull BlockPos clone() {
        return new BlockPos(this.worldKey, this.x, this.y, this.z);
    }
}
