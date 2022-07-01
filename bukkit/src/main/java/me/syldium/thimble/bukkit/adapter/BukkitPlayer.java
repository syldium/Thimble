package me.syldium.thimble.bukkit.adapter;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.syldium.thimble.bukkit.adventure.AdventureProvider.asEmitter;
import static me.syldium.thimble.bukkit.util.BukkitUtil.isWater;

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
        return this.platform.asAbstractLocation(this.handle.getLocation());
    }

    @Override
    public @NotNull PoolBlock getFirstLiquidBlock() {
        Block block = this.handle.getLocation().getBlock();
        while (block.getRelative(BlockFace.UP).isLiquid()) {
            block = block.getRelative(BlockFace.UP);
        }
        return new BukkitPoolBlock(block);
    }

    @Override
    public boolean teleport(@NotNull Location location) {
        return this.handle.teleport(this.platform.asPlatform(location));
    }

    @Override
    public @NotNull CompletableFuture<Boolean> teleportAsync(@NotNull Location location) {
        if (TELEPORT_ASYNC) {
            return this.handle.teleportAsync(this.platform.asPlatform(location));
        }
        return CompletableFuture.completedFuture(this.teleport(location));
    }

    @Override
    public boolean isInWater() {
        if (IN_WATER_METHOD) {
            return this.handle.isInWater();
        }
        return isWater(this.handle.getLocation().getBlock().getType());
    }

    @Override
    public void setMiniGameMode(boolean clearInventory) {
        if (clearInventory) {
            this.handle.setHealth(20D);
            this.handle.setFoodLevel(20);
            this.handle.getInventory().clear();
        }
        this.handle.setGameMode(GameMode.ADVENTURE);
    }

    @Override
    public void spectate() {
        this.handle.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public void sendExperienceChange(float percent, int level) {
        if (SEND_EXPERIENCE_CHANGE) {
            this.handle.sendExperienceChange(percent, level);
            return;
        }
        this.handle.setLevel(level);
        this.handle.setExp(percent);
    }

    @Override
    public boolean isVanished() {
        return isVanished(this.handle);
    }

    public static boolean isVanished(@NotNull Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull String name() {
        return this.handle.getName();
    }

    @Override
    public @NotNull UUID uuid() {
        return this.handle.getUniqueId();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.handle.hasPermission(permission);
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        if (emitter instanceof BukkitPlayer) {
            super.playSound(sound, asEmitter(((BukkitPlayer) emitter).handle));
        } else {
            super.playSound(sound);
        }
    }
}
