package me.syldium.thimble.api;

import me.syldium.thimble.api.util.BlockVector;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Location {

    private final UUID world;
    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;

    public Location(@NotNull UUID world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Location(@NotNull UUID world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public @NotNull UUID getWorldUUID() {
        return this.world;
    }

    public @NotNull Template[] asTemplates() {
        return this.asBlockPosition().asTemplates();
    }

    public @NotNull BlockVector asBlockPosition() {
        return new BlockVector((int) Math.floor(this.x), (int) Math.floor(this.y), (int) Math.floor(this.z));
    }

    public @NotNull Location up(int distance) {
        return distance == 0 ? this : new Location(this.world, this.x, this.y + distance, this.z, this.pitch, this.yaw);
    }

    public @NotNull Location down(int distance) {
        return this.up(-distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.x, this.x) == 0
                && Double.compare(location.y, this.y) == 0
                && Double.compare(location.z, this.z) == 0
                && Float.compare(location.pitch, this.pitch) == 0
                && Float.compare(location.yaw, this.yaw) == 0
                && this.world.equals(location.world);
    }

    @Override
    public int hashCode() {
        int hash = this.world.hashCode();
        hash = 53 * hash + (this.x != 0.0D ? hash(this.x) : 0);
        hash = 53 * hash + (this.y != 0.0D ? hash(this.y) : 0);
        hash = 53 * hash + (this.z != 0.0D ? hash(this.z) : 0);
        hash = 53 * hash + (this.pitch != 0.0f ? hash(this.pitch) : 0);
        hash = 53 * hash + (this.yaw != 0.0f ? hash(this.yaw) : 0);
        return hash;
    }

    private static int hash(double value) {
        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

    private static int hash(float value) {
        return Float.floatToIntBits(value * 663608941.737f);
    }
}
