package me.syldium.thimble.api.bukkit;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Location adapter for Bukkit.
 *
 * <p>Sample usage:
 * <pre>
 * Bukkit.getServicesManager().load(GameService.class).getArena("demo").ifPresent(arena -> {
 *     arena.setJumpLocation(BukkitAdapter.get().asAbstractLoc(player));
 *     if (arena.getPoolCenterPoint() != null) {
 *         Block block = BukkitAdapter.get().asBukkit(arena.getPoolCenterPoint(), arena.getJumpLocation().getWorldKey());
 *         block.setType(Material.EMERALD_BLOCK);
 *     }
 * });
 * </pre>
 */
public final class BukkitAdapter implements Listener {

    private static BukkitAdapter INSTANCE;
    private final Map<World, Key> worldKeys = new HashMap<>();
    private final Map<Key, World> keysToWorld = new HashMap<>();
    private final Function<World, Key> worldKeyFunction = world -> Key.key(world.getName());
    private final Function<Key, World> keyWorldFunction = key -> Bukkit.getWorld(Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value(): key.asString());

    /**
     * Internal! Creates a new global instance.
     *
     * @param plugin The Thimble plugin.
     */
    @ApiStatus.Internal
    public BukkitAdapter(@NotNull Plugin plugin) {
        if (INSTANCE != null) {
            throw new IllegalStateException("A BukkitAdapter instance already exists!");
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        INSTANCE = this;
    }

    /**
     * Gets the {@link BukkitAdapter} instance.
     *
     * @return The instance.
     */
    public static @NotNull BukkitAdapter get() {
        return INSTANCE;
    }

    /**
     * Converts a {@link org.bukkit.Location} from Bukkit to a {@link Location} for the plugin.
     *
     * @param bukkitLoc The Bukkit location.
     * @return A new location abstracted from Bukkit.
     */
    @Contract("_ -> new")
    public @NotNull Location asAbstract(@NotNull org.bukkit.Location bukkitLoc) {
        requireNonNull(bukkitLoc, "location");
        World world = requireNonNull(bukkitLoc.getWorld(), "world");
        return new Location(
                this.getWorldKey(world),
                bukkitLoc.getX(),
                bukkitLoc.getY(),
                bukkitLoc.getZ(),
                bukkitLoc.getPitch(),
                bukkitLoc.getYaw()
        );
    }

    /**
     * Converts a {@link Location} for the plugin from an {@link Entity}.
     *
     * @param bukkitEntity The Bukkit entity.
     * @return A new location abstracted from Bukkit.
     */
    @Contract("_ -> new")
    public @NotNull Location asAbstractLoc(@NotNull Entity bukkitEntity) {
        return this.asAbstract(requireNonNull(bukkitEntity, "entity").getLocation());
    }

    /**
     * Gets a {@link BlockPos} from a Bukkit {@link Block}.
     *
     * @param bukkitBlock The Bukkit block.
     * @return A new location abstracted from Bukkit.
     */
    @Contract("_ -> new")
    public @NotNull BlockPos asAbstract(@NotNull Block bukkitBlock) {
        requireNonNull(bukkitBlock, "block");
        return new BlockPos(
                this.getWorldKey(bukkitBlock.getWorld()),
                bukkitBlock.getX(),
                bukkitBlock.getY(),
                bukkitBlock.getZ()
        );
    }

    /**
     * Converts a {@link BlockPos} from the plugin to a {@link Block} for Bukkit.
     *
     * @param abstractPos A block position.
     * @return The relevant block.
     * @throws IllegalArgumentException When the world is not found.
     */
    public @NotNull Block asBukkit(@NotNull BlockPos abstractPos) {
        requireNonNull(abstractPos, "block position");
        World world = this.getWorldFromKey(abstractPos.worldKey());
        if (world == null) {
            throw new IllegalArgumentException(abstractPos.worldKey().asString() + " world does not exist!");
        }
        return world.getBlockAt(abstractPos.x(), abstractPos.y(), abstractPos.z());
    }

    /**
     * Converts a {@link BlockVector} and a world {@link Key} to a {@link Block} for Bukkit.
     *
     * @param abstractVector The plugin block vector.
     * @param worldKey The world resource key.
     * @return The relevant block.
     * @throws IllegalArgumentException When the world is not found.
     */
    public @NotNull Block asBukkit(@NotNull BlockVector abstractVector, @NotNull Key worldKey) {
        requireNonNull(abstractVector, "block vector");
        return this.asBukkit(new BlockPos(abstractVector, worldKey));
    }

    /**
     * Converts a {@link org.bukkit.util.BlockVector} from Bukkit to a {@link BlockVector} for the plugin.
     *
     * @param bukkitVector The Bukkit block vector.
     * @return A new location abstracted from Bukkit.
     */
    @Contract("_ -> new")
    public @NotNull BlockVector asAbstract(@NotNull org.bukkit.util.BlockVector bukkitVector) {
        requireNonNull(bukkitVector, "block vector");
        return new BlockVector(bukkitVector.getBlockX(), bukkitVector.getBlockY(), bukkitVector.getBlockZ());
    }

    /**
     * Gets a {@link World} from a resource {@link Key}.
     *
     * @param key The resource key.
     * @return The world if it exists.
     */
    public @Nullable World getWorldFromKey(@NotNull Key key) {
        return this.keysToWorld.computeIfAbsent(key, this.keyWorldFunction);
    }

    /**
     * Converts a {@link Location} from the plugin to a {@link org.bukkit.Location} for Bukkit.
     *
     * @param abstractLoc A location.
     * @return A new Bukkit location.
     */
    @Contract("_ -> new")
    public @NotNull org.bukkit.Location asBukkit(@NotNull Location abstractLoc) {
        requireNonNull(abstractLoc, "location");
        return new org.bukkit.Location(
                this.getWorldFromKey(abstractLoc.worldKey()),
                abstractLoc.x(),
                abstractLoc.y(),
                abstractLoc.z(),
                abstractLoc.yaw(),
                abstractLoc.pitch()
        );
    }

    /**
     * Gets the resource {@link Key} for a {@link World}.
     *
     * @param world The world.
     * @return The resource key.
     */
    public @NotNull Key getWorldKey(@NotNull World world) {
        return this.worldKeys.computeIfAbsent(world, this.worldKeyFunction);
    }

    @ApiStatus.Internal @EventHandler
    void onWorldUnload(WorldUnloadEvent event) {
        Key key = this.worldKeys.remove(event.getWorld());
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
