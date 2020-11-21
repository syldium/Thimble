package me.syldium.decoudre.bukkit.adapter;

import me.syldium.decoudre.bukkit.DeCoudreBootstrap;
import me.syldium.decoudre.common.adapter.PlayerAdapter;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.PoolBlock;
import me.syldium.decoudre.bukkit.command.BukkitSender;
import me.syldium.decoudre.bukkit.world.BukkitBlockData;
import me.syldium.decoudre.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

public class BukkitPlayerAdapter extends PlayerAdapter<org.bukkit.entity.Player, Location> {

    private static final BlockFace[] DIRECTIONS = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    private final DeCoudreBootstrap bootstrap;
    private final BukkitAudiences audiences;
    private final Map<org.bukkit.entity.Player, Player> players = new WeakHashMap<>();

    public BukkitPlayerAdapter(@NotNull DeCoudreBootstrap bootstrap, @NotNull BukkitAudiences audiences) {
        this.bootstrap = bootstrap;
        this.audiences = audiences;
    }

    @Override
    public boolean isDeCoudre(@NotNull PoolBlock abstracted) {
        Block block = ((BukkitPoolBlock) abstracted).getHandle();
        for (BlockFace direction : DIRECTIONS) {
            if (block.getRelative(direction).isPassable()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull BlockData getRandomWool() {
        Set<Material> wools = Tag.WOOL.getValues();
        int index = new Random().nextInt(wools.size());
        Iterator<Material> iter = wools.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return new BukkitBlockData(iter.next().createBlockData());
    }

    @Override
    public @NotNull org.bukkit.entity.Player asPlatform(@NotNull Player player) {
        return Objects.requireNonNull(this.bootstrap.getServer().getPlayer(player.uuid()));
    }

    @Override
    public @NotNull Player asAbstractPlayer(org.bukkit.entity.@NotNull Player player) {
        return this.players.computeIfAbsent(player, s -> new BukkitPlayer(this.bootstrap.getPlugin(), player, this.audiences.player(player), this));
    }

    @Override
    public @NotNull Location asPlatform(me.syldium.decoudre.api.@NotNull Location location) {
        return new Location(
                this.bootstrap.getServer().getWorld(location.getWorldUUID()),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    @Override
    public me.syldium.decoudre.api.@NotNull Location asAbstractLocation(@NotNull Location location) {
        return new me.syldium.decoudre.api.Location(
                location.getWorld().getUID(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getPitch(),
                location.getYaw()
        );
    }

    public @NotNull Sender asAbstractSender(@NotNull CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player) {
            return this.asAbstractPlayer((org.bukkit.entity.Player) sender);
        }
        return new BukkitSender(this.bootstrap.getPlugin(), sender, this.audiences.sender(sender));
    }
}
