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
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;

/**
 * <p><a href="https://github.com/EngineHub/WorldEdit">WorldEdit</a></p>
 *
 * <p>Adds `/th arena region` to define a pool area of an {@link Arena}.</p>
 */
class RegionCommand extends ChildCommand.One<Arena> {

    private static final boolean API_V7;

    private final WorldEditPlugin worldEdit;

    static {
        boolean v7 = false;
        try {
            v7 = Modifier.isPublic(Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter").getModifiers());
        } catch (ReflectiveOperationException ignored) { }
        API_V7 = v7;
    }

    RegionCommand(@NotNull Server server) {
        super("region", Arguments.arena(), null, Permission.arenaSetup());
        this.worldEdit = (WorldEditPlugin) server.getPluginManager().getPlugin("WorldEdit");
    }

    @Override @SuppressWarnings("unchecked")
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) {
        Player player = ((AbstractPlayer<Player>) sender).getHandle();

        if (API_V7) {
            this.runApi7(player, arena);
        } else {
            try {
                this.runApi6(player, arena);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

        return CommandResult.success(MessageKey.FEEDBACK_ARENA_SET_REGION);
    }

    public void runApi6(@NotNull Player player, @NotNull Arena arena) throws ReflectiveOperationException {
        // noinspection JavaReflectionMemberAccess
        Object selection = WorldEditPlugin.class.getMethod("getSelection", Player.class)
                .invoke(this.worldEdit, player);
        if (selection == null) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_REGION_NO_SELECTION);
        }

        Location min = (Location) selection.getClass().getMethod("getMinimumPoint").invoke(selection);
        Location max = (Location) selection.getClass().getMethod("getMaximumPoint").invoke(selection);
        arena.setPoolMinPoint(this.asVector3(min));
        arena.setPoolMaxPoint(this.asVector3(max));
    }

    public void runApi7(@NotNull Player player, @NotNull Arena arena) {
        BukkitPlayer wePlayer = BukkitAdapter.adapt(player);
        try {
            Region selection = this.worldEdit.getWorldEdit().getSessionManager().get(wePlayer).getSelection(wePlayer.getWorld());
            arena.setPoolMinPoint(this.asVector3(selection.getMinimumPoint()));
            arena.setPoolMaxPoint(this.asVector3(selection.getMaximumPoint()));
        } catch (IncompleteRegionException ex) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_REGION_NO_SELECTION);
        }
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof me.syldium.thimble.common.player.Player;
    }

    private @NotNull BlockVector asVector3(@NotNull com.sk89q.worldedit.math.BlockVector3 blockVector) {
        return new BlockVector(blockVector.getX(), blockVector.getY(), blockVector.getZ());
    }

    private @NotNull BlockVector asVector3(@NotNull Location location) {
        return me.syldium.thimble.api.bukkit.BukkitAdapter.get().asAbstractPos(location);
    }
}
