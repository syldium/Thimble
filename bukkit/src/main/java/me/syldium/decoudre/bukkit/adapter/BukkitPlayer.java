package me.syldium.decoudre.bukkit.adapter;

import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.player.AbstractPlayer;
import me.syldium.decoudre.common.world.PoolBlock;
import me.syldium.decoudre.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BukkitPlayer extends AbstractPlayer<Player> {

    private static final boolean IN_WATER_METHOD;

    static {
        boolean hasInWater = false;
        try {
            Player.class.getMethod("isInWater");
            hasInWater = true;
        } catch (NoSuchMethodException ignored) { }
        IN_WATER_METHOD = hasInWater;
    }

    private final BukkitPlayerAdapter platform;

    public BukkitPlayer(@NotNull DeCoudrePlugin plugin, @NotNull Player handle, @NotNull Audience audience, @NotNull BukkitPlayerAdapter platform) {
        super(plugin, handle, audience);
        this.platform = platform;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.platform.asAbstractLocation(this.getHandle().getLocation());
    }

    @Override
    public @NotNull PoolBlock getFirstLiquidBlock() {
        Block block = this.getHandle().getLocation().getBlock();
        while (block.getRelative(BlockFace.UP).isLiquid()) {
            block = block.getRelative(BlockFace.UP);
        }
        return new BukkitPoolBlock(block);
    }

    @SuppressWarnings("checkstyle:NoWhitespaceAfter")
    @Override
    public PoolBlock[] getBlocksBelow() {
        Player player = this.getHandle();
        org.bukkit.Location loc = player.getLocation().subtract(0, 0.2D, 0);
        Block block = loc.getBlock();
        if (!block.isPassable()) {
            return new PoolBlock[]{new BukkitPoolBlock(block)};
        }

        BoundingBox bb = player.getBoundingBox();
        World world = player.getWorld();

        int y = (int) loc.getY();
        return new PoolBlock[] {
                this.getBlockAt(world, bb.getMinX(), y, bb.getMinZ()),
                this.getBlockAt(world, bb.getMaxX(), y, bb.getMinZ()),
                this.getBlockAt(world, bb.getMaxX(), y, bb.getMaxZ()),
                this.getBlockAt(world, bb.getMinX(), y, bb.getMaxZ())
        };
    }

    private @NotNull PoolBlock getBlockAt(@NotNull World world, double x, int y, double z) {
        return new BukkitPoolBlock(world.getBlockAt(org.bukkit.Location.locToBlock(x), y, org.bukkit.Location.locToBlock(z)));
    }

    @Override
    public boolean teleport(@NotNull Location location) {
        return this.getHandle().teleport(this.platform.asPlatform(location));
    }

    @Override
    public boolean isInWater() {
        if (IN_WATER_METHOD) {
            return this.getHandle().isInWater();
        }
        return this.getHandle().getLocation().getBlock().getType() == Material.WATER;
    }

    @Override
    public @NotNull String name() {
        return this.getHandle().getName();
    }

    @Override
    public @NotNull UUID uuid() {
        return this.getHandle().getUniqueId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        return this.getHandle().equals(((BukkitPlayer) o).getHandle());
    }

    @Override
    public int hashCode() {
        return this.getHandle().hashCode();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.getHandle().hasPermission(permission);
    }
}
