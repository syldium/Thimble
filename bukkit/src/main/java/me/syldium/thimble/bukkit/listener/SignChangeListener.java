package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleGameState;
import me.syldium.thimble.api.bukkit.BukkitGameChangeStateEvent;
import me.syldium.thimble.api.bukkit.BukkitGameEndEvent;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.util.SignAction;
import me.syldium.thimble.common.util.EnumUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class SignChangeListener implements Listener {

    private final ThBukkitPlugin plugin;
    private final Set<Material> clickable;

    public SignChangeListener(@NotNull ThBukkitPlugin plugin, @NotNull Set<@NotNull Material> clickable) {
        this.plugin = plugin;
        this.clickable = clickable;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("thimble.sign.place")) return;

        String[] lines = event.getLines();
        if (lines.length < 2 || !containsPrefix(lines[0])) return;

        Block block = event.getBlock();
        BlockPos position = new BlockPos(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());

        Optional<ThimbleArena> arena = this.plugin.getGameService().getArena(ChatColor.stripColor(lines[1]));
        if (arena.isPresent()) {
            this.plugin.getGameService().addSign(position, arena.get());
            event.setLine(0, ChatColor.DARK_GREEN + lines[0]);
        } else {
            SignAction action = EnumUtil.valueOf(SignAction.class, lines[1], null);
            if (action == null) {
                this.plugin.sendFeedback(event.getPlayer(), CommandResult.error(MessageKey.FEEDBACK_GAME_UNKNOWN));
            } else {
                this.plugin.getGameService().addSign(position, action);
                event.setLine(0, ChatColor.DARK_GREEN + lines[0]);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameChange(BukkitGameChangeStateEvent event) {
        this.updateSigns(event.getArena(), event.getNewState());
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameEnd(BukkitGameEndEvent event) {
        this.updateSigns(event.getArena(), ThimbleGameState.WAITING);
    }

    private void updateSigns(@NotNull ThimbleArena arena, @NotNull ThimbleGameState state) {
        UUID worldUUID = requireNonNull(arena.getSpawnLocation(), "arena spawn location").getWorldUUID();
        World world = requireNonNull(Bukkit.getWorld(worldUUID), "arena spawn world");

        List<BlockPos> removable = new LinkedList<>();
        for (BlockPos pos : this.plugin.getGameService().getArenaSigns(arena)) {
            if (!world.isChunkLoaded(pos.getChunkX(), pos.getChunkZ())) {
                continue;
            }

            Block block = world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            if (this.clickable.contains(block.getType())) {
                BlockState blockState = block.getState();
                if (blockState instanceof Sign) {
                    Sign sign = (Sign) blockState;
                    sign.setLine(2, state.name());
                    sign.update();
                }
            } else {
                removable.add(pos);
            }
        }

        for (BlockPos pos : removable) {
            this.plugin.getGameService().removeSign(pos);
        }
    }

    public static boolean containsPrefix(@NotNull String line) {
        String toLowerCase = line.toLowerCase(Locale.ROOT);
        return toLowerCase.contains("[thimble]")
                || toLowerCase.contains("[deacoudre]")
                || toLowerCase.contains("[déàcoudre]");
    }
}
