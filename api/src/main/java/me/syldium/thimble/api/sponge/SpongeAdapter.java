package me.syldium.thimble.api.sponge;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.util.WorldKey;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Location adapter for the Sponge API 7.
 */
public final class SpongeAdapter {

    private static SpongeAdapter INSTANCE;
    private final Map<World, WorldKey> worldKeys = new HashMap<>();
    private final Map<WorldKey, World> keysToWorld = new HashMap<>();
    private final Function<World, WorldKey> worldKeyFunction = world -> new WorldKey(world.getName());

    /**
     * Internal! Creates a new global instance.
     *
     * @param plugin The Thimble plugin.
     */
    @ApiStatus.Internal
    public SpongeAdapter(@NotNull Object plugin) {
        if (INSTANCE != null) {
            throw new IllegalStateException("A SpongeAdapter instance already exists!");
        }
        Sponge.getEventManager().registerListeners(plugin, this);
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

    /**
     * Converts a {@link org.spongepowered.api.world.Location} from Sponge to a {@link Location} for the plugin.
     *
     * @param spongeLoc The Sponge location.
     * @return A new location abstracted from Sponge.
     */
    @Contract("_ -> new")
    public @NotNull Location asAbstract(@NotNull org.spongepowered.api.world.Location<World> spongeLoc) {
        return this.asAbstract(spongeLoc, Vector3d.ZERO);
    }

    /**
     * Converts a {@link org.spongepowered.api.world.Location} from Sponge to a {@link Location} for the plugin.
     *
     * @param spongeLoc The Sponge location.
     * @param headRotation The head rotation.
     * @return A new location abstracted from Sponge.
     */
    @Contract("_, _ -> new")
    public @NotNull Location asAbstract(@NotNull org.spongepowered.api.world.Location<World> spongeLoc, @NotNull Vector3d headRotation) {
        requireNonNull(spongeLoc, "location");
        return new Location(
                this.getWorldKey(spongeLoc.getExtent()),
                spongeLoc.getX(),
                spongeLoc.getY(),
                spongeLoc.getZ(),
                (float) headRotation.getX(),
                (float) headRotation.getY()
        );
    }

    /**
     * Converts a {@link Transform} to a {@link Location} for the plugin.
     *
     * @param transform The transform.
     * @return A new location abstracted from Sponge.
     */
    @Contract("_ -> new")
    public @NotNull Location asAbstract(@NotNull Transform<World> transform) {
        requireNonNull(transform, "transform");
        return new Location(
                this.getWorldKey(transform.getExtent()),
                transform.getPosition().getX(),
                transform.getPosition().getY(),
                transform.getPosition().getZ(),
                (float) transform.getPitch(),
                (float) transform.getYaw()
        );
    }

    /**
     * Converts a {@link Vector3i} from Sponge to a {@link BlockVector} for the plugin.
     *
     * @param spongeVec The Sponge vector.
     * @return A new block vector.
     */
    @Contract("_ -> new")
    public @NotNull BlockVector asAbstract(@NotNull Vector3i spongeVec) {
        requireNonNull(spongeVec, "vector");
        return new BlockVector(
                spongeVec.getX(),
                spongeVec.getY(),
                spongeVec.getZ()
        );
    }

    /**
     * Converts a {@link Vector3i} from Sponge to a {@link BlockVector} for the plugin.
     *
     * @param spongeVec The Sponge vector.
     * @return A new block vector.
     */
    @Contract("_ -> new")
    public @NotNull BlockVector asAbstract(@NotNull Vector3d spongeVec) {
        requireNonNull(spongeVec, "vector");
        return new BlockVector(
                spongeVec.getFloorX(),
                spongeVec.getFloorY(),
                spongeVec.getFloorZ()
        );
    }

    /**
     * Gets a {@link World} from a {@link WorldKey}.
     *
     * @param key The resource key.
     * @return The world if it exists.
     */
    public @Nullable World getWorldFromKey(@NotNull WorldKey key) {
        return this.keysToWorld.computeIfAbsent(key, this::keyToWorld);
    }

    private @Nullable World keyToWorld(@NotNull WorldKey worldKey) {
        String worldName = Key.MINECRAFT_NAMESPACE.equals(worldKey.namespace()) ? worldKey.value() : worldKey.asString();
        return Sponge.getServer().getWorld(worldName).orElse(null);
    }

    /**
     * Converts a {@link Location} from the plugin to a {@link org.spongepowered.api.world.Location} for Sponge.
     *
     * @param abstractLoc A location.
     * @return A new Sponge location.
     */
    @Contract("_ -> new")
    public @NotNull org.spongepowered.api.world.Location<World> asSponge(@NotNull Location abstractLoc) {
        requireNonNull(abstractLoc, "location");
        return new org.spongepowered.api.world.Location<>(
                this.getWorldFromKey(abstractLoc.worldKey()),
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
     * Moves the entity to the specified location, and sets the rotation.
     *
     * @param entity The entity to move.
     * @param location The location to set.
     */
    public void setLocation(@NotNull Entity entity, @NotNull Location location) {
        requireNonNull(location, "location");
        requireNonNull(entity, "entity").setLocationAndRotation(this.asSponge(location), this.asHeadRotation(location));
    }

    /**
     * Gets the location of the living entity.
     *
     * @param entity The living entity.
     * @return A new location object.
     */
    @Contract("_ -> new")
    public @NotNull Location getLocation(@NotNull Living entity) {
        requireNonNull(entity, "living entity");
        return this.asAbstract(entity.getLocation(), entity.getHeadRotation());
    }

    /**
     * Gets the location of a locatable thing.
     *
     * @param locatable The locatable thing.
     * @return A new location object.
     */
    @Contract("_ -> new")
    public @NotNull Location getLocation(@NotNull Locatable locatable) {
        if (locatable instanceof Living) {
            return this.getLocation((Living) locatable);
        }
        requireNonNull(locatable, "locatable");
        return this.asAbstract(locatable.getLocation());
    }

    /**
     * Gets the resource {@link WorldKey} for a {@link World}.
     *
     * @param world The world.
     * @return The resource key.
     */
    public @NotNull WorldKey getWorldKey(@NotNull World world) {
        return this.worldKeys.computeIfAbsent(world, this.worldKeyFunction);
    }

    @Listener @ApiStatus.Internal
    public void onWorldLoad(LoadWorldEvent event) {
        this.removeWorldKey(event.getTargetWorld());
    }

    @Listener @ApiStatus.Internal
    public void onWorldUnload(UnloadWorldEvent event) {
        this.removeWorldKey(event.getTargetWorld());
    }

    private void removeWorldKey(@NotNull World world) {
        Key key = this.worldKeys.remove(world);
        if (key != null) {
            this.keysToWorld.remove(key);
        }
    }

    /**
     * Internal! Removes the global instance.
     */
    @ApiStatus.Internal
    public static void unregister() {
        INSTANCE = null;
    }
}
