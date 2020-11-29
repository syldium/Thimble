package me.syldium.decoudre.bukkit.adapter;

import me.syldium.decoudre.bukkit.DeBukkitPlugin;
import me.syldium.decoudre.bukkit.DeCoudreBootstrap;
import me.syldium.decoudre.bukkit.command.BukkitSender;
import me.syldium.decoudre.bukkit.util.BlockSelectionInventory;
import me.syldium.decoudre.bukkit.util.BukkitUtil;
import me.syldium.decoudre.bukkit.world.BukkitBlockData;
import me.syldium.decoudre.common.adapter.PlayerAdapter;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.Player;
import me.syldium.decoudre.common.world.BlockData;
import me.syldium.decoudre.common.world.PoolBlock;
import me.syldium.decoudre.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

public class BukkitPlayerAdapter implements PlayerAdapter<org.bukkit.entity.Player, Location> {

    public static final BlockFace[] DIRECTIONS = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static final Set<Material> WOOLS_TYPES = BukkitUtil.getAllMaterialsMatching(material -> material.name().endsWith("WOOL"));

    private final DeCoudreBootstrap bootstrap;
    private final BukkitAudiences audiences;
    private final Map<org.bukkit.entity.Player, Player> players = new WeakHashMap<>();
    private final BlockSelectionInventory inventory;

    public BukkitPlayerAdapter(@NotNull DeBukkitPlugin plugin, @NotNull DeCoudreBootstrap bootstrap, @NotNull BukkitAudiences audiences) {
        this.bootstrap = bootstrap;
        this.audiences = audiences;
        this.inventory = new BlockSelectionInventory(plugin);
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
        int index = new Random().nextInt(WOOLS_TYPES.size());
        Iterator<Material> iter = WOOLS_TYPES.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return new BukkitBlockData(iter.next().createBlockData());
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

    @Override
    public void openBlockSelectionInventory(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
        this.inventory.open(this.asPlatform(player), inGamePlayer);
    }

    public @NotNull Sender asAbstractSender(@NotNull CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player) {
            return this.asAbstractPlayer((org.bukkit.entity.Player) sender);
        }
        return new BukkitSender(this.bootstrap.getPlugin(), sender, this.audiences.sender(sender));
    }
}
