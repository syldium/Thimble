package me.syldium.decoudre.bukkit.adapter;

import me.syldium.decoudre.api.BlockVector;
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
import me.syldium.decoudre.common.world.PoolBlock;
import me.syldium.decoudre.bukkit.world.BukkitPoolBlock;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class BukkitPlayerAdapter implements PlayerAdapter<org.bukkit.entity.Player, Location> {

    public static final BlockFace[] DIRECTIONS = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    private final DeCoudreBootstrap bootstrap;
    private final BukkitAudiences audiences;
    private final Map<org.bukkit.entity.Player, Player> players = new WeakHashMap<>();
    private final List<BukkitBlockData> blockDatas;
    private final BlockSelectionInventory inventory;

    public BukkitPlayerAdapter(@NotNull DeBukkitPlugin plugin, @NotNull DeCoudreBootstrap bootstrap, @NotNull BukkitAudiences audiences) {
        this.bootstrap = bootstrap;
        this.audiences = audiences;

        Pattern[] patterns = plugin.getConfig().getStringList("blocks").stream()
                .map(Pattern::compile)
                .toArray(Pattern[]::new);
        Set<Material> materials = BukkitUtil.getAllBlocksMatching(plugin.getLogger(), patterns);
        if (materials.isEmpty()) {
            plugin.getLogger().severe("The list of blocks in the configuration file is empty/invalid!"
                    + " This will cause an error every time a player tries to join an arena.");
        }
        this.blockDatas = materials.stream().map(BukkitBlockData::new).collect(Collectors.toList());
        this.inventory = new BlockSelectionInventory(plugin, this);
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
    public @NotNull Player asAbstractPlayer(org.bukkit.entity.@NotNull Player player) {
        return this.players.computeIfAbsent(player, s -> new BukkitPlayer(this.bootstrap.getPlugin(), player, this.audiences.player(player), this));
    }

    @Override
    public @NotNull Set<@NotNull BlockVector> getRemainingWaterBlocks(@NotNull UUID worldUUID, @NotNull BlockVector minPoint, @NotNull BlockVector maxPoint) {
        World world = requireNonNull(this.bootstrap.getServer().getWorld(worldUUID), "world");
        Set<BlockVector> set = new HashSet<>();
        for (int x = minPoint.getX(); x <= maxPoint.getX(); x++) {
            for (int y = minPoint.getY(); y <= maxPoint.getY(); y++) {
                for (int z = minPoint.getZ(); z <= maxPoint.getZ(); z++) {
                    if (world.getBlockAt(x, y, z).isLiquid()) {
                        set.add(new BlockVector(x, y, z));
                    }
                }
            }
        }
        return set;
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
