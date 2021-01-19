package me.syldium.thimble.api.bukkit;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.util.WorldKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Location adapter for Bukkit.
 *
 * <p>Sample usage:
 * <pre>
 * Bukkit.getServicesManager().load(GameService.class).arena("demo").ifPresent(arena -&gt; {
 *     arena.setJumpLocation(BukkitAdapter.get().asAbstractLoc(player));
 *     if (arena.poolCenterPoint() != null) {
 *         Block block = BukkitAdapter.get().asBukkit(arena.poolCenterPoint(), arena.jumpLocation().worldKey());
 *         block.setType(Material.EMERALD_BLOCK);
 *     }
 * });
 * </pre>
 */
public final class BukkitAdapter implements Listener {

    private static BukkitAdapter INSTANCE;
    private final Map<World, WorldKey> worldKeys = new HashMap<>();
    private final Map<WorldKey, World> keysToWorld = new HashMap<>();
    private final Function<World, WorldKey> worldKeyFunction = world -> new WorldKey(world.getName());
    private final Function<WorldKey, World> keyWorldFunction = key -> Bukkit.getWorld(Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.asString());

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
     * Converts a {@link org.bukkit.Location} from Bukkit to a {@link BlockPos} for the plugin.
     *
     * @param bukkitLoc The Bukkit location.
     * @return A new block position.
     */
    @Contract("_ -> new")
    public @NotNull BlockPos asAbstractPos(@NotNull org.bukkit.Location bukkitLoc) {
        requireNonNull(bukkitLoc, "location");
        World world = requireNonNull(bukkitLoc.getWorld(), "world");
        return new BlockPos(
                this.getWorldKey(world),
                bukkitLoc.getBlockX(),
                bukkitLoc.getBlockY(),
                bukkitLoc.getBlockZ()
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
    public @NotNull Block asBukkit(@NotNull BlockVector abstractVector, @NotNull WorldKey worldKey) {
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
     * Gets a {@link World} from a resource {@link WorldKey}.
     *
     * @param key The resource key.
     * @return The world if it exists.
     */
    public @Nullable World getWorldFromKey(@NotNull WorldKey key) {
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
     * Gets the resource {@link WorldKey} for a {@link World}.
     *
     * @param world The world.
     * @return The resource key.
     */
    public @NotNull WorldKey getWorldKey(@NotNull World world) {
        return this.worldKeys.computeIfAbsent(world, this.worldKeyFunction);
    }

    /**
     * Returns online players from their uuid.
     *
     * @param playerUniqueIds Players' unique identifiers.
     * @return A new list of online players.
     */
    public @NotNull List<@NotNull Player> asPlayers(@NotNull Iterable<UUID> playerUniqueIds) {
        List<Player> players = new LinkedList<>();
        for (UUID uuid : playerUniqueIds) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * Returns a new list of online players in this arena.
     *
     * @param game A thimble game.
     * @return A new list of online players.
     */
    public @NotNull List<@NotNull Player> asPlayers(@NotNull ThimbleGame game) {
        return this.asPlayers(game.uuidSet());
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        this.removeWorldKey(event.getWorld());
    }

    @EventHandler
    private void onWorldUnload(WorldUnloadEvent event) {
        this.removeWorldKey(event.getWorld());
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
