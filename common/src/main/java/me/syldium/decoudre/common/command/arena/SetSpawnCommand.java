package me.syldium.decoudre.common.command.arena;

import me.syldium.decoudre.api.Location;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.command.abstraction.spec.Arguments;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.common.player.MessageKey;
import me.syldium.decoudre.common.player.Player;
import org.jetbrains.annotations.NotNull;

class SetSpawnCommand extends ChildCommand.One<Arena> {

    SetSpawnCommand() {
        super("setSpawn", Arguments.arena(), MessageKey.HELP_SET_SPAWN, Permission.arenaSetup("set.spawn"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) throws CommandException {
        Location loc = ((Player) sender).getLocation();
        arena.setSpawnLocation(loc);
        return CommandResult.success(
                MessageKey.FEEDBACK_ARENA_SET_SPAWN,
                loc.asTemplates()
        );
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
