package me.syldium.decoudre.sponge.adapter;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.player.AbstractPlayer;
import me.syldium.decoudre.common.world.PoolBlock;
import me.syldium.decoudre.sponge.world.SpongePoolBlock;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class SpongePlayer extends AbstractPlayer<Player> {

    private final SpongePlayerAdapter platform;

    public SpongePlayer(@NotNull DeCoudrePlugin plugin, @NotNull Player handle, @NotNull Audience audience, @NotNull SpongePlayerAdapter platform) {
        super(plugin, handle, audience);
        this.platform = platform;
    }

    @Override
    public @NotNull me.syldium.decoudre.api.Location getLocation() {
        return this.platform.asAbstractLocation(this.getHandle().getLocation(), this.getHandle().getHeadRotation());
    }

    @Override
    public boolean teleport(@NotNull me.syldium.decoudre.api.Location location) {
        return this.getHandle().setLocationAndRotation(this.platform.asPlatform(location), this.platform.asHeadRotation(location));
    }

    @SuppressWarnings("checkstyle:NoWhitespaceAfter")
    @Override
    public @NotNull PoolBlock[] getBlocksBelow() {
        Player player = this.getHandle();
        Location<World> loc = player.getLocation().sub(0, 0.2D, 0);
        AABB bb = player.getBoundingBox().orElseThrow(RuntimeException::new);
        World world = player.getWorld();

        int y = (int) loc.getY();
        return new PoolBlock[] {
                this.getBlockAt(world, bb.getMin().getX(), y, bb.getMin().getZ()),
                this.getBlockAt(world, bb.getMax().getX(), y, bb.getMin().getZ()),
                this.getBlockAt(world, bb.getMax().getX(), y, bb.getMax().getZ()),
                this.getBlockAt(world, bb.getMin().getX(), y, bb.getMax().getZ())
        };
    }

    private @NotNull PoolBlock getBlockAt(@NotNull World world, double x, int y, double z) {
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        return new SpongePoolBlock(world.getBlock(blockX, y, blockZ), new Location<>(world, blockX, y, blockZ));
    }

    @Override
    public @NotNull PoolBlock getFirstLiquidBlock() {
        Location<World> location = this.getHandle().getLocation();
        while (this.isLiquid(location.getBlockRelative(Direction.UP).getBlock())) {
            location = location.getBlockRelative(Direction.UP);
        }
        return new SpongePoolBlock(location.getBlock(), location);
    }

    private boolean isLiquid(@NotNull BlockState blockState) {
        Optional<MatterProperty> matter = blockState.getProperty(MatterProperty.class);
        return matter.isPresent() && matter.get().getValue() == MatterProperty.Matter.LIQUID;
    }

    @Override
    public boolean isInWater() {
        return this.getHandle().getLocation().getBlock().getType().equals(BlockTypes.WATER);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.getHandle().hasPermission(permission);
    }

    @Override
    public @NotNull String name() {
        return this.getHandle().getName();
    }

    @Override
    public @NotNull UUID uuid() {
        return this.getHandle().getUniqueId();
    }
}
