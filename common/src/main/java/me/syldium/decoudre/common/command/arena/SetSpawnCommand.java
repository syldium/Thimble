package me.syldium.decoudre.common.command.arena;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.command.abstraction.spec.Arguments;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.common.player.Message;
import me.syldium.decoudre.common.player.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand extends ChildCommand.One<Arena> {

    public SetSpawnCommand() {
        super("setSpawn", Arguments.arena(), Message.SET_ARENA_SPAWN, Permission.ADMIN);
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) throws CommandException {
        arena.setSpawnLocation(((Player) sender).getLocation());
        return CommandResult.SUCCESS;
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
