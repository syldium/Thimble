package me.syldium.thimble.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class BlockVector implements Serializable, Cloneable {

    private final int x;
    private final int y;
    private final int z;

    public BlockVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getChunkX() {
        return this.x >> 4;
    }

    public int getY() {
        return this.y;
    }

    public int getChunkY() {
        return this.y >> 4;
    }

    public int getZ() {
        return this.z;
    }

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
    public @NotNull BlockVector clone() {
        return new BlockVector(this.x, this.y, this.z);
    }
}
