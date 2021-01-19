package me.syldium.thimble.sponge.adapter;

import com.flowpowered.math.vector.Vector3d;
import me.syldium.thimble.api.sponge.SpongeAdapter;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.util.WorldKey;
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
    private final SpongeAdapter locationAdapter;

    public SpongePlayerAdapter(@NotNull ThSpongePlugin plugin, @NotNull SpongeAudiences audiences) {
        this.plugin = plugin;
        this.audiences = audiences;

        for (BlockState state : this.plugin.getRegistry().getAllOf(BlockState.class)) {
            if (state.getType().equals(BlockTypes.WOOL)) {
                this.blockDatas.add(new SpongeBlockData(state));
            }
        }
        this.inventory = new BlockSelectionInventory(plugin);
        this.locationAdapter = new SpongeAdapter(plugin);
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
    public void clearPool(@NotNull WorldKey worldKey, @NotNull Map<BlockVector, BlockData> blocks) {
        World world = this.plugin.getServer().getWorld(worldKey.value()).orElseThrow(() -> new RuntimeException("A world was expected here."));
        for (Map.Entry<BlockVector, BlockData> entry : blocks.entrySet()) {
            BlockVector pos = entry.getKey();
            world.setBlock(pos.x(), pos.y(), pos.z(), ((SpongeBlockData) entry.getValue()).getHandle());
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
    public @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull WorldKey worldKey, @NotNull BlockVector minPoint, @NotNull BlockVector maxPoint) {
        World world = this.plugin.getServer().getWorld(worldKey.value()).orElseThrow(() -> new RuntimeException("A world was expected here."));
        Set<BlockVector> set = new HashSet<>();
        for (int x = minPoint.x(); x <= maxPoint.x(); x++) {
            for (int y = minPoint.y(); y <= maxPoint.y(); y++) {
                for (int z = minPoint.z(); z <= maxPoint.z(); z++) {
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
        return this.locationAdapter.asSponge(location);
    }

    @Override
    public me.syldium.thimble.api.@NotNull Location asAbstractLocation(@NotNull Location<World> location) {
        return this.locationAdapter.asAbstract(location);
    }

    public me.syldium.thimble.api.@NotNull Location asAbstractLocation(@NotNull Transform<World> transform) {
        return this.locationAdapter.asAbstract(transform);
    }

    public @NotNull BlockVector asBlockVector(@NotNull Vector3d position) {
        return this.locationAdapter.asAbstract(position);
    }

    @Override
    public void openBlockSelectionInventory(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
        this.inventory.open(player, inGamePlayer);
    }

    public me.syldium.thimble.api.@NotNull Location asAbstractLocation(@NotNull Location<World> location, @NotNull Vector3d headRotation) {
        return this.locationAdapter.asAbstract(location, headRotation);
    }

    public @NotNull Vector3d asHeadRotation(@NotNull me.syldium.thimble.api.Location location) {
        return this.locationAdapter.asHeadRotation(location);
    }

    public @NotNull Sender asAbstractSender(@NotNull CommandSource source) {
        if (source instanceof Player) {
            return this.asAbstractPlayer((Player) source);
        }
        return new SpongeSender(this.plugin, source, this.audiences.receiver(source));
    }
}
