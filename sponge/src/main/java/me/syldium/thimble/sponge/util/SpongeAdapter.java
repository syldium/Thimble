package me.syldium.thimble.sponge.util;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.WorldKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import static java.util.Objects.requireNonNull;

/**
 * Location adapter for the Sponge API 14.
 */
public final class SpongeAdapter {

    private static SpongeAdapter INSTANCE;

    /**
     * Internal! Creates a new global instance.
     *
     * @param plugin The Thimble plugin.
     */
    @ApiStatus.Internal
    public SpongeAdapter(@NotNull Object plugin) {
        if (INSTANCE != null) {
            throw new IllegalStateException("A Sponge8Adapter instance already exists!");
        }
        //Sponge.getEventManager().registerListeners(plugin, this);
        INSTANCE = this;
    }

    /**
     * Gets the {@link SpongeAdapter} instance.
     *
     * @return The instance.
     */
    public static @NotNull SpongeAdapter get() {
        return INSTANCE;
    }

    public static void unregister() {
        INSTANCE = null;
    }

    /**
     * Converts a {@link ServerLocation} from Sponge to a {@link Location} for the plugin.
     *
     * @param spongeLoc The Sponge location.
     * @return A new location abstracted from Sponge.
     */
    @Contract("_ -> new")
    public @NotNull Location asAbstract(@NotNull ServerLocation spongeLoc) {
        return this.asAbstract(spongeLoc, Vector3d.ZERO);
    }

    /**
     * Converts a {@link ServerLocation} from Sponge to a {@link Location} for the plugin.
     *
     * @param spongeLoc The Sponge location.
     * @param headRotation The head rotation.
     * @return A new location abstracted from Sponge.
     */
    @Contract("_, _ -> new")
    public @NotNull Location asAbstract(@NotNull ServerLocation spongeLoc, @NotNull Vector3d headRotation) {
        requireNonNull(spongeLoc, "location");
        return new Location(
                this.worldKey(spongeLoc.worldKey()),
                spongeLoc.x(),
                spongeLoc.y(),
                spongeLoc.z(),
                (float) headRotation.x(),
                (float) headRotation.y()
        );
    }

    /**
     * Converts a {@link Location} from the plugin to a {@link ServerLocation} for Sponge.
     *
     * @param abstractLoc A location.
     * @return A new Sponge location.
     */
    @Contract("_ -> new")
    public @NotNull ServerLocation asSponge(@NotNull Location abstractLoc) {
        requireNonNull(abstractLoc, "location");
        return ServerLocation.of(
                this.resourceKey(abstractLoc.worldKey()),
                abstractLoc.x(),
                abstractLoc.y(),
                abstractLoc.z()
        );
    }

    /**
     * Converts a {@link Location} from the plugin to a head rotation.
     *
     * @param location A location.
     * @return A new head rotation vector.
     */
    @Contract("_ -> new")
    public @NotNull Vector3d asHeadRotation(@NotNull Location location) {
        requireNonNull(location, "location");
        return new Vector3d(location.pitch(), location.yaw(), 0);
    }

    /**
     * Gets the {@link WorldKey} for a {@link ResourceKey}.
     *
     * @param key The resource key.
     * @return The world key.
     */
    @Contract("_ -> new")
    public @NotNull WorldKey worldKey(@NotNull ResourceKey key) {
        return new WorldKey(key.namespace(), key.value());
    }

    /**
     * Gets the {@link ResourceKey} for a {@link WorldKey}.
     *
     * @param key The world key.
     * @return The resource key.
     */
    @Contract("_ -> new")
    public @NotNull ResourceKey resourceKey(@NotNull WorldKey key) {
        requireNonNull(key, "world key");
        return ResourceKey.of(key.namespace(), key.value());
    }
}
