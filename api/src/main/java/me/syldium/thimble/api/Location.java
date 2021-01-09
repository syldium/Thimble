package me.syldium.thimble.api;

import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Represents a 3-dimensional position in a world.
 */
public class Location implements Serializable {

    private final Key worldKey;
    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;

    public Location(@NotNull Key worldKey, double x, double y, double z, float pitch, float yaw) {
        this.worldKey = requireNonNull(worldKey, "world resource key");
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Location(@NotNull Key worldKey, double x, double y, double z) {
        this(worldKey, x, y, z, 0, 0);
    }

    public Location(@NotNull Key worldKey, @NotNull BlockVector position) {
        this(worldKey, position.x(), position.y(), position.z());
    }

    public Location(@NotNull BlockPos position) {
        this(position.worldKey(), position.x(), position.y(), position.z());
    }

    /**
     * Gets the x-coordinate.
     *
     * @return x-coordinate
     */
    public double x() {
        return this.x;
    }

    /**
     * Gets the y-coordinate.
     *
     * @return y-coordinate
     */
    public double y() {
        return this.y;
    }

    /**
     * Gets the z-coordinate.
     *
     * @return z-coordinate
     */
    public double z() {
        return this.z;
    }

    /**
     * Gets the pitch of this location, in degrees.
     *
     * @return The pitch.
     */
    public float pitch() {
        return this.pitch;
    }

    /**
     * Gets the yaw of this location, in degrees.
     *
     * @return The yaw.
     */
    public float yaw() {
        return this.yaw;
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
        return distance == 0 ? this : new Location(this.worldKey, this.x, this.y + distance, this.z, this.pitch, this.yaw);
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
        if (!Objects.equals(this.worldKey, other.worldKey)) {
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
        return square(this.x - vector.x()) + square(this.y - vector.y()) + square(this.z - vector.z());
    }

    public double horizontalDistanceSquared(@NotNull BlockVector vector) {
        return square(this.x - vector.x()) + square(this.z - vector.z());
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
                && this.worldKey.equals(location.worldKey);
    }

    @Override
    public int hashCode() {
        int hash = this.worldKey.hashCode();
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
                "world=" + this.worldKey.asString() +
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
