package me.syldium.thimble.bukkit.listener;

import me.syldium.thimble.api.bukkit.BukkitAdapter;
import me.syldium.thimble.api.util.BlockPos;
import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.api.arena.ThimbleState;
import me.syldium.thimble.api.bukkit.BukkitGameChangeStateEvent;
import me.syldium.thimble.api.bukkit.BukkitGameEndEvent;
import me.syldium.thimble.api.util.WorldKey;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.util.SignAction;
import me.syldium.thimble.common.util.EnumUtil;
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

import static java.util.Objects.requireNonNull;

public class SignChangeListener implements Listener {

    private final ThBukkitPlugin plugin;
    private final Set<Material> clickable;
    private final boolean updateSigns;

    public SignChangeListener(@NotNull ThBukkitPlugin plugin, @NotNull Set<@NotNull Material> clickable) {
        this.plugin = plugin;
        this.clickable = clickable;
        this.updateSigns = plugin.getConfig().getBoolean("update-signs", true);
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("thimble.sign.place")) return;

        String[] lines = event.getLines();
        if (lines.length < 2 || !containsPrefix(lines[0])) return;

        Block block = event.getBlock();
        BlockPos position = BukkitAdapter.get().asAbstract(block);

        Optional<ThimbleArena> arena = this.plugin.getGameService().arena(ChatColor.stripColor(lines[1]));
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
        this.updateSigns(event.getArena(), ThimbleState.WAITING);
    }

    private void updateSigns(@NotNull ThimbleArena arena, @NotNull ThimbleState state) {
        if (!this.updateSigns) {
            return;
        }

        WorldKey worldKey = requireNonNull(arena.spawnLocation(), "arena spawn location").worldKey();
        World world = requireNonNull(BukkitAdapter.get().getWorldFromKey(worldKey), "arena spawn world");

        List<BlockPos> removable = new LinkedList<>();
        for (BlockPos pos : this.plugin.getGameService().arenaSigns(arena)) {
            if (!world.isChunkLoaded(pos.chunkX(), pos.chunkZ())) {
                continue;
            }

            Block block = world.getBlockAt(pos.x(), pos.y(), pos.z());
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
