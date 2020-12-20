package me.syldium.thimble.common.command.arena;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

class SetWaitCommand extends ChildCommand.One<Arena> {

    SetWaitCommand() {
        super("setWait", Arguments.arena(), MessageKey.HELP_SET_WAIT, Permission.arenaSetup("set.wait"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) throws CommandException {
        Location loc = ((Player) sender).getLocation();
        arena.setWaitLocation(loc);
        if (arena.getSpawnLocation() == null) {
            arena.setSpawnLocation(loc);
        }
        return CommandResult.success(
                MessageKey.FEEDBACK_ARENA_SET_WAIT,
                loc.asTemplates()
        );
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
