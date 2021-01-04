package me.syldium.thimble.api;

import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a 3-dimensional position in a world.
 */
public class Location implements Serializable {

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

    public Location(@NotNull UUID world, @NotNull BlockVector position) {
        this(world, position.getX(), position.getY(), position.getZ());
    }

    public Location(@NotNull BlockPos position) {
        this(position.getWorldUUID(), position.getX(), position.getY(), position.getZ());
    }

    /**
     * Gets the x-coordinate.
     *
     * @return x-coordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate.
     *
     * @return y-coordinate
     */
    public double getY() {
        return this.y;
    }

    /**
     * Gets the z-coordinate.
     *
     * @return z-coordinate
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Gets the pitch of this location, in degrees.
     *
     * @return The pitch.
     */
    public float getPitch() {
        return this.pitch;
    }

    /**
     * Gets the yaw of this location, in degrees.
     *
     * @return The yaw.
     */
    public float getYaw() {
        return this.yaw;
    }

    /**
     * Gets the unique identifier of the world.
     *
     * @return The {@link UUID}.
     */
    public @NotNull UUID getWorldUUID() {
        return this.world;
    }

    /**
     * Returns a template array from the location.
     *
     * @return A template array (floored coordinates).
     */
    public @NotNull Template[] asTemplates() {
        return this.asBlockPosition().asTemplates();
    }

    /**
     * Returns the block position to this location.
     *
     * @return A new block vector.
     */
    public @NotNull BlockVector asBlockPosition() {
        return new BlockVector((int) Math.floor(this.x), (int) Math.floor(this.y), (int) Math.floor(this.z));
    }

    /**
     * Increases the y-coordinate of this location.
     *
     * @param distance The distance to add.
     * @return A new location, or this if {@code distance = 0}
     */
    public @NotNull Location up(int distance) {
        return distance == 0 ? this : new Location(this.world, this.x, this.y + distance, this.z, this.pitch, this.yaw);
    }

    public @NotNull Location down(int distance) {
        return this.up(-distance);
    }

    /**
     * Gets the squared distance between this location and another.
     *
     * @param other The other location.
     * @return The distance.
     * @throws IllegalArgumentException If the worlds are different.
     */
    public double distanceSquared(@NotNull Location other) {
        if (!Objects.equals(this.world, other.world)) {
            throw new IllegalArgumentException("Cannot determine the distance between two different worlds!");
        }
        return square(this.x - other.x) + square(this.y - other.y) + square(this.z - other.z);
    }

    /**
     * Gets the squared distance between this location and a block vector.
     *
     * @param vector The block vector.
     * @return The distance.
     */
    public double distanceSquared(@NotNull BlockVector vector) {
        return square(this.x - vector.getX()) + square(this.y - vector.getY()) + square(this.z - vector.getZ());
    }

    public double horizontalDistanceSquared(@NotNull BlockVector vector) {
        return square(this.x - vector.getX()) + square(this.z - vector.getZ());
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

    @Override
    public String toString() {
        return "Location{" +
                "world=" + this.world.toString().substring(0, 8) +
                ", x=" + this.x +
                ", y=" + this.y +
                ", z=" + this.z +
                ", pitch=" + this.pitch +
                ", yaw=" + this.yaw +
                '}';
    }

    private static int hash(double value) {
        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

    private static int hash(float value) {
        return Float.floatToIntBits(value * 663608941.737f);
    }

    private static double square(double x) {
        return x * x;
    }
}
