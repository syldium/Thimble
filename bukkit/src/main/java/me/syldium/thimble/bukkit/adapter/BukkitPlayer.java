package me.syldium.thimble.bukkit.adapter;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
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

    public BukkitPlayer(@NotNull ThimblePlugin plugin, @NotNull Player handle, @NotNull Audience audience, @NotNull BukkitPlayerAdapter platform) {
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

    @Override
    public boolean teleport(@NotNull Location location) {
        this.getHandle().setVelocity(new Vector(0, 0, 0));
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
    public void sendExperienceChange(float percent, int level) {
        this.getHandle().sendExperienceChange(percent, level);
    }

    @Override
    public void sendRealExperience() {
        this.getHandle().setExp(this.getHandle().getExp());
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
