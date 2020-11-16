package me.syldium.decoudre.common.command.game;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.spec.Arguments;
import me.syldium.decoudre.common.command.abstraction.spec.CommandGuard;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.common.player.Message;
import me.syldium.decoudre.common.player.Player;
import org.jetbrains.annotations.NotNull;

public class JoinCommand extends ChildCommand.One<Arena> {

    public JoinCommand() {
        super("join", Arguments.arena(), Message.JOIN_ARENA, Permission.PLAYER);
        this.commandGuard = CommandGuard.EXCEPT_NOT_IN_GAME;
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) throws CommandException {
        arena.addPlayer(sender.getUuid());
        sender.sendMessage(Message.ARENA_JOINED.format());
        return CommandResult.SUCCESS;
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
