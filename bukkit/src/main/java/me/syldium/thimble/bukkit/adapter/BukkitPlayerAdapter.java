package me.syldium.thimble.bukkit.adapter;

import me.syldium.thimble.api.bukkit.BukkitAdapter;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.bukkit.ThBootstrap;
import me.syldium.thimble.bukkit.command.BukkitSender;
import me.syldium.thimble.bukkit.util.BlockSelectionInventory;
import me.syldium.thimble.bukkit.util.BukkitUtil;
import me.syldium.thimble.bukkit.world.BukkitBlockData;
import me.syldium.thimble.common.adapter.PlayerAdapter;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class BukkitPlayerAdapter implements PlayerAdapter<org.bukkit.entity.Player, Location> {

    public static final BlockFace[] DIRECTIONS = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    private final ThBootstrap bootstrap;
    private final BukkitAudiences audiences;
    private final Map<UUID, BukkitPlayer> players = new HashMap<>();
    private final List<BukkitBlockData> blockDatas;
    private final BlockSelectionInventory inventory;
    private final BukkitAdapter locationAdapter;

    public BukkitPlayerAdapter(@NotNull ThBukkitPlugin plugin, @NotNull ThBootstrap bootstrap, @NotNull BukkitAudiences audiences) {
        this.bootstrap = bootstrap;
        this.audiences = audiences;

        Set<Material> materials = BukkitUtil.getAllBlocksMatching(plugin.getLogger(), plugin.getConfig().getStringList("blocks"));
        if (materials.isEmpty()) {
            plugin.getLogger().severe("The list of blocks in the configuration file is empty/invalid!"
                    + " This will cause an error every time a player tries to join an arena.");
        }
        this.blockDatas = materials.stream().map(BukkitBlockData::new).collect(Collectors.toList());
        this.inventory = new BlockSelectionInventory(plugin, this);
        this.locationAdapter = new BukkitAdapter(bootstrap);
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
    public @NotNull List<BukkitBlockData> getAvailableBlocks() {
        return this.blockDatas;
    }

    @Override
    public void clearPool(@NotNull Key worldKey, @NotNull Map<BlockVector, BlockData> blocks) {
        World world = requireNonNull(this.locationAdapter.getWorldFromKey(worldKey), "world");
        for (Map.Entry<BlockVector, BlockData> entry : blocks.entrySet()) {
            BlockVector pos = entry.getKey();
            Block block = world.getBlockAt(pos.x(), pos.y(), pos.z());
            block.setBlockData(((BukkitBlockData) entry.getValue()).getHandle());
        }
    }

    @Override
    public @Nullable BukkitPlayer getPlayer(@NotNull UUID uuid) {
        BukkitPlayer p = this.players.get(uuid);
        if (p != null) {
            return p;
        }

        Player player = this.bootstrap.getServer().getPlayer(uuid);
        if (player == null) {
            return null;
        }
        p = new BukkitPlayer(this.bootstrap.getPlugin(), player, this.audiences.player(player), this);
        this.players.put(player.getUniqueId(), p);
        return p;
    }

    @Override
    public @NotNull BukkitPlayer asAbstractPlayer(org.bukkit.entity.@NotNull Player player) {
        BukkitPlayer p = this.players.get(player.getUniqueId());
        if (p != null) {
            return p;
        }
        p = new BukkitPlayer(this.bootstrap.getPlugin(), player, this.audiences.player(player), this);
        this.players.put(player.getUniqueId(), p);
        return p;
    }

    public void unregisterPlayer(@NotNull UUID uuid) {
        this.players.remove(uuid);
    }

    @Override
    public @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull Key worldKey, @NotNull BlockVector minPoint, @NotNull BlockVector maxPoint) {
        World world = requireNonNull(this.locationAdapter.getWorldFromKey(worldKey), "world");
        Set<BlockVector> set = new HashSet<>();
        for (int x = minPoint.x(); x <= maxPoint.x(); x++) {
            for (int z = minPoint.z(); z <= maxPoint.z(); z++) {
                BlockVector block = this.getHighestWaterBlock(world, x, z, minPoint.y(), maxPoint.y());
                if (block != null) {
                    set.add(block);
                }
            }
        }
        return set;
    }

    private @Nullable BlockVector getHighestWaterBlock(@NotNull World world, int x, int z, int minY, int maxY) {
        Block block = world.getBlockAt(x, minY, z);
        boolean liquid = BukkitUtil.containsLiquid(block);
        int y = minY;
        while (++y <= maxY) {
            block = world.getBlockAt(x, y, z);
            if (liquid) {
                if (!BukkitUtil.containsLiquid(block)) {
                    return new BlockVector(x, y - 1, z);
                }
            } else {
                liquid = BukkitUtil.containsLiquid(block);
            }
        }
        return liquid ? new BlockVector(x, maxY, z) : null;
    }

    @Override
    public @NotNull Location asPlatform(me.syldium.thimble.api.@NotNull Location location) {
        return this.locationAdapter.asBukkit(location);
    }

    private @Nullable World getWorldFromKey(@NotNull Key key) {
        return this.bootstrap.getServer().getWorld(key.asString());
    }

    @Override
    public me.syldium.thimble.api.@NotNull Location asAbstractLocation(@NotNull Location location) {
        return this.locationAdapter.asAbstract(location);
    }

    public @NotNull BlockVector asBlockVector(@NotNull Location location) {
        return new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public void openBlockSelectionInventory(@NotNull org.bukkit.entity.Player player, @NotNull InGamePlayer inGamePlayer) {
        this.inventory.open(player, inGamePlayer);
    }

    public @NotNull Sender asAbstractSender(@NotNull CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player) {
            return this.asAbstractPlayer((org.bukkit.entity.Player) sender);
        }
        return new BukkitSender(this.bootstrap.getPlugin(), sender, this.audiences.sender(sender));
    }
}
