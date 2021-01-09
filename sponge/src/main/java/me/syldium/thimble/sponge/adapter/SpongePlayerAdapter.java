package me.syldium.thimble.sponge.adapter;

import com.flowpowered.math.vector.Vector3d;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.adapter.PlayerAdapter;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.sponge.ThSpongePlugin;
import me.syldium.thimble.sponge.command.SpongeSender;
import me.syldium.thimble.sponge.util.BlockSelectionInventory;
import me.syldium.thimble.sponge.world.SpongeBlockData;
import me.syldium.thimble.sponge.world.SpongePoolBlock;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SpongePlayerAdapter implements PlayerAdapter<Player, Location<World>> {

    private static final Direction[] DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    private final SpongeAudiences audiences;
    private final Map<UUID, SpongePlayer> players = new HashMap<>();
    private final ThSpongePlugin plugin;
    private final List<SpongeBlockData> blockDatas = new ArrayList<>();
    private final BlockSelectionInventory inventory;

    public SpongePlayerAdapter(@NotNull ThSpongePlugin plugin, @NotNull SpongeAudiences audiences) {
        this.plugin = plugin;
        this.audiences = audiences;

        for (BlockState state : this.plugin.getRegistry().getAllOf(BlockState.class)) {
            if (state.getType().equals(BlockTypes.WOOL)) {
                this.blockDatas.add(new SpongeBlockData(state));
            }
        }
        this.inventory = new BlockSelectionInventory(plugin);
    }
    
    @Override
    public boolean isDeCoudre(@NotNull PoolBlock abstracted) {
        Location<World> location = ((SpongePoolBlock) abstracted).getLocation();
        for (Direction direction : DIRECTIONS) {
            BlockType type = location.getBlockRelative(direction).getBlock().getType();
            if (type.equals(BlockTypes.WATER) || type.equals(BlockTypes.FLOWING_WATER)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull List<SpongeBlockData> getAvailableBlocks() {
        return this.blockDatas;
    }

    @Override
    public void clearPool(@NotNull UUID worldUUID, @NotNull Map<BlockVector, BlockData> blocks) {
        World world = this.plugin.getServer().getWorld(worldUUID).orElseThrow(() -> new RuntimeException("A world was expected here."));
        for (Map.Entry<BlockVector, BlockData> entry : blocks.entrySet()) {
            BlockVector pos = entry.getKey();
            world.setBlock(pos.getX(), pos.getY(), pos.getZ(), ((SpongeBlockData) entry.getValue()).getHandle());
        }
    }

    @Override
    public @Nullable SpongePlayer getPlayer(@NotNull UUID uuid) {
        SpongePlayer p = this.players.get(uuid);
        if (p != null) {
            return p;
        }

        Optional<Player> playerOpt = this.plugin.getServer().getPlayer(uuid);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            p = new SpongePlayer(this.plugin, player, this.audiences.player(player), this);
            this.players.put(player.getUniqueId(), p);
            return p;
        }
        return null;
    }

    @Override
    public @NotNull SpongePlayer asAbstractPlayer(@NotNull Player player) {
        SpongePlayer p = this.players.get(player.getUniqueId());
        if (p != null) {
            return p;
        }
        p = new SpongePlayer(this.plugin, player, this.audiences.player(player), this);
        this.players.put(player.getUniqueId(), p);
        return p;
    }

    public void unregisterPlayer(@NotNull UUID uuid) {
        this.players.remove(uuid);
    }

    @Override
    public @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull UUID worldUUID, @NotNull BlockVector minPoint, @NotNull BlockVector maxPoint) {
        World world = this.plugin.getServer().getWorld(worldUUID).orElseThrow(() -> new RuntimeException("A world was expected here."));
        Set<BlockVector> set = new HashSet<>();
        for (int x = minPoint.getX(); x <= maxPoint.getX(); x++) {
            for (int y = minPoint.getY(); y <= maxPoint.getY(); y++) {
                for (int z = minPoint.getZ(); z <= maxPoint.getZ(); z++) {
                    BlockType type = world.getBlock(x, y, z).getType();
                    if (type.equals(BlockTypes.WATER) || type.equals(BlockTypes.FLOWING_WATER)) {
                        set.add(new BlockVector(x, y, z));
                    }
                }
            }
        }
        return set;
    }

    @Override
    public @NotNull Location<World> asPlatform(me.syldium.thimble.api.@NotNull Location location) {
        Optional<World> world = this.plugin.getServer().getWorld(location.getWorldUUID());
        return new Location<>(
                world.orElseThrow(() -> new RuntimeException("A world was expected here.")),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

    @Override
    public me.syldium.thimble.api.@NotNull Location asAbstractLocation(@NotNull Location<World> location) {
        return this.asAbstractLocation(location, Vector3d.ZERO);
    }

    public me.syldium.thimble.api.@NotNull Location asAbstractLocation(@NotNull Transform<World> transform) {
        Optional<World> world = this.plugin.getServer().getWorld(transform.getExtent().getUniqueId());
        return new me.syldium.thimble.api.Location(
                world.orElseThrow(() -> new RuntimeException("A world was expected here.")).getUniqueId(),
                transform.getPosition().getX(),
                transform.getPosition().getY(),
                transform.getPosition().getZ(),
                (float) transform.getPitch(),
                (float) transform.getYaw()
        );
    }

    public @NotNull BlockVector asBlockVector(@NotNull Vector3d position) {
        return new BlockVector(position.getFloorX(), position.getFloorY(), position.getFloorZ());
    }

    @Override
    public void openBlockSelectionInventory(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
        this.inventory.open(player, inGamePlayer);
    }

    public me.syldium.thimble.api.@NotNull Location asAbstractLocation(@NotNull Location<World> location, @NotNull Vector3d headRotation) {
        return new me.syldium.thimble.api.Location(
                location.getExtent().getUniqueId(),
                location.getX(),
                location.getY(),
                location.getZ(),
                (float) headRotation.getX(),
                (float) headRotation.getY()
        );
    }

    public @NotNull Vector3d asHeadRotation(@NotNull me.syldium.thimble.api.Location location) {
        return new Vector3d(location.getPitch(), location.getYaw(), 0);
    }

    public @NotNull Sender asAbstractSender(@NotNull CommandSource source) {
        if (source instanceof Player) {
            return this.asAbstractPlayer((Player) source);
        }
        return new SpongeSender(this.plugin, source, this.audiences.receiver(source));
    }
}
