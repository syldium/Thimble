package me.syldium.decoudre.sponge.adapter;

import com.flowpowered.math.vector.Vector3d;
import me.syldium.decoudre.common.adapter.PlayerAdapter;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.PoolBlock;
import me.syldium.decoudre.sponge.DeSpongePlugin;
import me.syldium.decoudre.sponge.command.SpongeSender;
import me.syldium.decoudre.sponge.world.SpongeBlockData;
import me.syldium.decoudre.sponge.world.SpongePoolBlock;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.WeakHashMap;

public class SpongePlayerAdapter implements PlayerAdapter<Player, Location<World>> {

    private static final Direction[] DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    private final SpongeAudiences audiences;
    private final Map<Player, me.syldium.decoudre.common.player.Player> players = new WeakHashMap<>();
    private final DeSpongePlugin plugin;
    private final List<BlockState> wools = new ArrayList<>();

    public SpongePlayerAdapter(@NotNull DeSpongePlugin plugin, @NotNull SpongeAudiences audiences) {
        this.plugin = plugin;
        this.audiences = audiences;

        for (BlockState state : this.plugin.getRegistry().getAllOf(BlockState.class)) {
            if (state.getType().equals(BlockTypes.WOOL)) {
                this.wools.add(state);
            }
        }
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
    public @NotNull BlockData getRandomWool() {
        return new SpongeBlockData(this.wools.get(new Random().nextInt(this.wools.size())));
    }

    @Override
    public @NotNull Player asPlatform(me.syldium.decoudre.common.player.@NotNull Player player) {
        return this.plugin.getServer().getPlayer(player.uuid()).orElseThrow(() -> new RuntimeException("A player was expected here."));
    }

    @Override
    public me.syldium.decoudre.common.player.@NotNull Player asAbstractPlayer(@NotNull Player player) {
        return this.players.computeIfAbsent(player, s -> new SpongePlayer(this.plugin, player, this.audiences.player(player), this));
    }

    @Override
    public @NotNull Location<World> asPlatform(me.syldium.decoudre.api.@NotNull Location location) {
        Optional<World> world = this.plugin.getServer().getWorld(location.getWorldUUID());
        return new Location<>(
                world.orElseThrow(() -> new RuntimeException("A world was expected here.")),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

    @Override
    public me.syldium.decoudre.api.@NotNull Location asAbstractLocation(@NotNull Location<World> location) {
        return this.asAbstractLocation(location, Vector3d.ZERO);
    }

    @Override
    public void openBlockSelectionInventory(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
         // TODO
    }

    public me.syldium.decoudre.api.@NotNull Location asAbstractLocation(@NotNull Location<World> location, @NotNull Vector3d headRotation) {
        return new me.syldium.decoudre.api.Location(
                location.getExtent().getUniqueId(),
                location.getX(),
                location.getY(),
                location.getZ(),
                (float) headRotation.getX(),
                (float) headRotation.getY()
        );
    }

    public @NotNull Vector3d asHeadRotation(@NotNull me.syldium.decoudre.api.Location location) {
        return new Vector3d(location.getPitch(), location.getYaw(), 0);
    }

    public @NotNull Sender asAbstractSender(@NotNull CommandSource source) {
        if (source instanceof Player) {
            return this.asAbstractPlayer((Player) source);
        }
        return new SpongeSender(this.plugin, source, this.audiences.receiver(source));
    }
}
