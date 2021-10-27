package me.syldium.thimble.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.stream.Stream;

import static net.kyori.adventure.text.minimessage.Template.template;

/**
 * Defines an immutable block position relative to the current world.
 */
public class BlockVector implements Examinable, Serializable, Cloneable {

    /** x-coordinate. */
    protected final int x;

    /** y-coordinate. */
    protected final int y;

    /** z-coordinate. */
    protected final int z;

    /**
     * Constructs a new block vector with the given coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     */
    public BlockVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the x-coordinate.
     *
     * @return x-coordinate
     */
    public int x() {
        return this.x;
    }

    /**
     * Gets the x-coordinate of the chunk where the block is located.
     *
     * @return x-coordinate
     */
    public int chunkX() {
        return this.x >> 4;
    }

    /**
     * Gets the y-coordinate.
     *
     * @return y-coordinate
     */
    public int y() {
        return this.y;
    }

    /**
     * Gets the y-coordinate of the chunk where the block is located.
     *
     * @return y-coordinate
     */
    public int chunkY() {
        return this.y >> 4;
    }

    /**
     * Gets the z-coordinate.
     *
     * @return z-coordinate
     */
    public int z() {
        return this.z;
    }

    /**
     * Gets the z-coordinate of the chunk where the block is located.
     *
     * @return z-coordinate
     */
    public int chunkZ() {
        return this.z >> 4;
    }

    /**
     * Adds values to this vector.
     *
     * @param x The amount to add to the x value.
     * @param y The amount to add to the y value.
     * @param z The amount to add to the z value.
     * @return A new vector.
     */
    public @NotNull BlockVector add(int x, int y, int z) {
        return new BlockVector(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Performs scalar multiplication on this vector.
     *
     * @param amount The scalar to multiply by.
     * @return A new vector.
     */
    public @NotNull BlockVector multiply(int amount) {
        return new BlockVector(this.x * amount, this.y * amount, this.z * amount);
    }

    /**
     * Computes the cross product of two vectors.
     *
     * @param other The other vector for which to compute the cross product.
     * @return A new vector representing the cross product.
     */
    public @NotNull BlockVector crossProduct(@NotNull BlockVector other) {
        return new BlockVector(
                (this.y * other.z) - (this.z * other.y),
                (this.z * other.x) - (this.x * other.z),
                (this.x * other.y) - (this.y * other.x)
        );
    }

    /**
     * Computes the squared distance with another {@link BlockVector}.
     *
     * @param other A block vector.
     * @return The squared distance.
     */
    public int distanceSquared(@NotNull BlockVector other) {
        return square(this.x - other.x) + square(this.y - other.y) + square(this.z - other.z);
    }

    /**
     * Returns a vector using {@link Math#max(int, int)} on each value.
     * 
     * @param other A block vector.
     * @return A new block vector.
     */
    public @NotNull BlockVector max(@NotNull BlockVector other) {
        return new BlockVector(Math.max(this.x, other.x), Math.max(this.y, other.y), Math.max(this.z, other.z));
    }

    /**
     * Returns a vector using {@link Math#min(int, int)} on each value.
     *
     * @param other A block vector.
     * @return A new block vector.
     */
    public @NotNull BlockVector min(@NotNull BlockVector other) {
        return new BlockVector(Math.min(this.x, other.x), Math.min(this.y, other.y), Math.min(this.z, other.z));
    }

    /**
     * Returns a template array from the block vector.
     *
     * @return A template array.
     */
    public @NotNull Template[] asTemplates() {
        //CHECKSTYLE:OFF
        return new Template[]{
                template("x", Component.text(this.x)),
                template("y", Component.text(this.y)),
                template("z", Component.text(this.z))
        };
        //CHECKSTYLE:ON
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockVector that = (BlockVector) o;
        return this.x == that.x
                && this.y == that.y
                && this.z == that.z;
    }

    @Override
    public int hashCode() {
        int result = this.x;
        result = 31 * result + this.y;
        result = 31 * result + this.z;
        return result;
    }

    @Override
    public String toString() {
        return "BlockVector{" + this.x + "," + this.y + "," + this.z + "}";
    }

    @Override
    public @NotNull BlockVector clone() {
        return new BlockVector(this.x, this.y, this.z);
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("x", this.x),
                ExaminableProperty.of("y", this.y),
                ExaminableProperty.of("z", this.z)
        );
    }

    private static int square(int n) {
        return n * n;
    }
}
