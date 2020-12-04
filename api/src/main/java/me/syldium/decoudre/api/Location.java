package me.syldium.decoudre.api;

import net.kyori.adventure.text.Component;
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
        //CHECKSTYLE:OFF
        return new Template[]{
                Template.of("x", Component.text((int) Math.floor(this.x))),
                Template.of("y", Component.text((int) Math.floor(this.y))),
                Template.of("z", Component.text((int) Math.floor(this.z)))
        };
        //CHECKSTYLE:ON
    }
}
