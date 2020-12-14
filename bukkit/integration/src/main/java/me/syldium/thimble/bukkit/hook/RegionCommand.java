package me.syldium.thimble.bukkit.hook;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import me.syldium.thimble.api.util.BlockVector;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.AbstractPlayer;
import me.syldium.thimble.common.player.MessageKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * <p><a href="https://github.com/EngineHub/WorldEdit">WorldEdit</a></p>
 *
 * <p>Adds `/th arena region` to define a pool area of an {@link Arena}.</p>
 */
class RegionCommand extends ChildCommand.One<Arena> {

    private final WorldEditPlugin worldEdit;

    RegionCommand(@NotNull Server server) {
        super("region", Arguments.arena(), null, Permission.arenaSetup());
        this.worldEdit = (WorldEditPlugin) server.getPluginManager().getPlugin("WorldEdit");
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) {
        // noinspection unchecked
        BukkitPlayer player = BukkitAdapter.adapt(((AbstractPlayer<Player>) sender).getHandle());
        try {
            Region selection = this.worldEdit.getWorldEdit().getSessionManager().get(player).getSelection(player.getWorld());
            arena.setPoolMinPoint(this.asVector3(selection.getMinimumPoint()));
            arena.setPoolMaxPoint(this.asVector3(selection.getMaximumPoint()));
        } catch (IncompleteRegionException ex) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_REGION_NO_SELECTION);
        }

        return CommandResult.success(MessageKey.FEEDBACK_ARENA_SET_REGION);
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof me.syldium.thimble.common.player.Player;
    }

    private @NotNull BlockVector asVector3(@NotNull com.sk89q.worldedit.math.BlockVector3 blockVector) {
        return new BlockVector(blockVector.getX(), blockVector.getY(), blockVector.getZ());
    }
}
