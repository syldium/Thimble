package me.syldium.thimble.bukkit.adapter;

import me.syldium.thimble.api.bukkit.BukkitAdapter;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.bukkit.ThBootstrap;
import me.syldium.thimble.bukkit.adventure.AdventureProvider;
import me.syldium.thimble.bukkit.command.BukkitSender;
import me.syldium.thimble.bukkit.util.BlockSelectionInventory;
import me.syldium.thimble.bukkit.util.BukkitUtil;
import me.syldium.thimble.bukkit.util.CraftBukkitFacet;
import me.syldium.thimble.bukkit.world.BukkitBlockData;
import me.syldium.thimble.common.adapter.PlayerAdapter;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.config.ConfigManager;
import me.syldium.thimble.common.listener.Reloadable;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.media.Scoreboard;
import me.syldium.thimble.common.world.BlockData;
import me.syldium.thimble.common.world.PoolBlock;
import me.syldium.thimble.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static me.syldium.thimble.bukkit.util.BukkitUtil.isWater;
import static me.syldium.thimble.bukkit.world.BukkitBlockData.IS_FLAT;

public class BukkitPlayerAdapter implements PlayerAdapter<org.bukkit.entity.Player, Location>, Reloadable {

    public static final BlockFace[] DIRECTIONS = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    private final ThBootstrap bootstrap;
    private final AdventureProvider audiences;
    private final Map<UUID, BukkitPlayer> players = new HashMap<>();
    private final List<BukkitBlockData> blockDatas = new ArrayList<>();
    private final BlockSelectionInventory inventory;
    private final BukkitAdapter locationAdapter;
    private final CraftBukkitFacet facet = new CraftBukkitFacet();
    private @Nullable BukkitBlockData thimbleBlock;

    public BukkitPlayerAdapter(@NotNull ThBukkitPlugin plugin, @NotNull ThBootstrap bootstrap, @NotNull AdventureProvider audiences) {
        this.bootstrap = bootstrap;
        this.audiences = audiences;

        this.reload(plugin.getConfigManager());
        this.inventory = new BlockSelectionInventory(plugin, this);
        this.locationAdapter = new BukkitAdapter(bootstrap);
    }

    @Override
    public boolean isDeCoudre(@NotNull PoolBlock abstracted) {
        Block block = ((BukkitPoolBlock) abstracted).getHandle();
        for (BlockFace direction : DIRECTIONS) {
            if (IS_FLAT ? block.getRelative(direction).isPassable() : isWater(block.getRelative(direction).getType())) {
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
    public @Nullable BukkitBlockData getThimbleBlock() {
        return this.thimbleBlock;
    }

    @Override
    public void clearPool(@NotNull WorldKey worldKey, @NotNull Map<BlockVector, BlockData> blocks) {
        World world = requireNonNull(this.locationAdapter.worldFromKey(worldKey), "world");
        for (Map.Entry<BlockVector, BlockData> entry : blocks.entrySet()) {
            BlockVector pos = entry.getKey();
            Block block = world.getBlockAt(pos.x(), pos.y(), pos.z());
            ((BukkitBlockData) entry.getValue()).setBlock(block);
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
    public @Nullable BukkitPlayer getPlayer(@NotNull String name) {
        final Player player = this.bootstrap.getServer().getPlayerExact(name);
        if (player == null) {
            return null;
        }
        return this.getPlayer(player.getUniqueId());
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
    public @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull WorldKey worldKey, @NotNull BlockVector minPoint, @NotNull BlockVector maxPoint) {
        World world = requireNonNull(this.locationAdapter.worldFromKey(worldKey), "world");
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
    public void openBlockSelectionInventory(@NotNull Player player, @NotNull InGamePlayer inGamePlayer) {
        this.inventory.open(player, inGamePlayer);
    }

    @Override
    public void setScoreboard(@NotNull Scoreboard scoreboard, me.syldium.thimble.common.player.@NotNull Player player) {
        this.facet.setScoreboard(this.asPlatform(player), scoreboard);
    }

    @Override
    public void hideScoreboard(@NotNull Scoreboard scoreboard, me.syldium.thimble.common.player.@NotNull Player player) {
        this.facet.removeScoreboard(this.asPlatform(player), scoreboard);
    }

    public @NotNull Sender asAbstractSender(@NotNull CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player) {
            return this.asAbstractPlayer((org.bukkit.entity.Player) sender);
        }
        return new BukkitSender(this.bootstrap.getPlugin(), sender, this.audiences.sender(sender));
    }

    @Override
    public void reload(@NotNull ConfigManager<?> configManager) {
        Set<Material> materials = BukkitUtil.getAllBlocksMatching(this.bootstrap.getLogger(), this.bootstrap.getConfig().getStringList("blocks"));
        if (materials.isEmpty()) {
            this.bootstrap.getLogger().severe("The list of blocks in the configuration file is empty/invalid!"
                    + " This will cause an error every time a player tries to join an arena.");
        }
        this.blockDatas.clear();
        for (Material material : materials) {
            this.blockDatas.addAll(BukkitBlockData.buildAll(material));
        }
        Material thimbleBlock = BukkitUtil.getBlock(this.bootstrap.getConfig().getString("thimble-block"), this.bootstrap.getLogger());
        if (thimbleBlock != null) {
            this.thimbleBlock = BukkitBlockData.build(thimbleBlock);
        }
    }
}
