package me.syldium.thimble.bukkit.adapter;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.audience.Audience;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitPlayer extends AbstractPlayer<Player> {

    private static final boolean IN_WATER_METHOD;
    private static final boolean TELEPORT_ASYNC;
    private static final boolean SEND_EXPERIENCE_CHANGE;

    static {
        boolean hasInWater = false;
        try {
            Player.class.getMethod("isInWater");
            hasInWater = true;
        } catch (NoSuchMethodException ignored) { }
        IN_WATER_METHOD = hasInWater;
        boolean teleportAsync = false;
        try {
            Entity.class.getMethod("teleportAsync", org.bukkit.Location.class);
            teleportAsync = true;
        } catch (NoSuchMethodException ignored) { }
        TELEPORT_ASYNC = teleportAsync;
        boolean sendExperienceChange = false;
        try {
            Player.class.getMethod("sendExperienceChange", float.class, int.class);
            sendExperienceChange = true;
        } catch (NoSuchMethodException ignored) { }
        SEND_EXPERIENCE_CHANGE = sendExperienceChange;
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
    public @NotNull CompletableFuture<Boolean> teleport(@NotNull Location location) {
        this.getHandle().setVelocity(new Vector(0, 0, 0));
        org.bukkit.Location bukkitLoc = this.platform.asPlatform(location);
        if (TELEPORT_ASYNC) {
            return this.getHandle().teleportAsync(bukkitLoc);
        }
        return CompletableFuture.completedFuture(this.getHandle().teleport(bukkitLoc));
    }

    @Override
    public boolean isInWater() {
        if (IN_WATER_METHOD) {
            return this.getHandle().isInWater();
        }
        return this.getHandle().getLocation().getBlock().getType() == Material.WATER;
    }

    @Override
    public void setMiniGameMode() {
        this.getHandle().getInventory().clear();
        this.getHandle().setGameMode(GameMode.ADVENTURE);
    }

    @Override
    public void spectate() {
        this.getHandle().setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public void sendExperienceChange(float percent, int level) {
        if (SEND_EXPERIENCE_CHANGE) {
            this.getHandle().sendExperienceChange(percent, level);
            return;
        }
        this.getHandle().setLevel(level);
        this.getHandle().setExp(percent);
    }

    @Override
    public boolean isVanished() {
        for (MetadataValue meta : this.getHandle().getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
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
    public boolean hasPermission(@NotNull String permission) {
        return this.getHandle().hasPermission(permission);
    }
}
