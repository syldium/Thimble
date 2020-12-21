package me.syldium.thimble.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Defines a block position relative to the current world.
 */
public class BlockVector implements Examinable, Serializable, Cloneable {

    protected final int x;
    protected final int y;
    protected final int z;

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
    public int getX() {
        return this.x;
    }

    /**
     * Gets the x-coordinate of the chunk where the block is located.
     *
     * @return x-coordinate
     */
    public int getChunkX() {
        return this.x >> 4;
    }

    /**
     * Gets the y-coordinate.
     *
     * @return y-coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Gets the y-coordinate of the chunk where the block is located.
     *
     * @return y-coordinate
     */
    public int getChunkY() {
        return this.y >> 4;
    }

    /**
     * Gets the z-coordinate.
     *
     * @return z-coordinate
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Gets the z-coordinate of the chunk where the block is located.
     *
     * @return z-coordinate
     */
    public int getChunkZ() {
        return this.z >> 4;
    }

    public @NotNull Template[] asTemplates() {
        //CHECKSTYLE:OFF
        return new Template[]{
                Template.of("x", Component.text(this.x)),
                Template.of("y", Component.text(this.y)),
                Template.of("z", Component.text(this.z))
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
}
