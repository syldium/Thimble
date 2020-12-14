package me.syldium.thimble.common.command.game;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.command.abstraction.spec.CommandGuard;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

public class JoinCommand extends ChildCommand.One<Arena> {

    public JoinCommand() {
        super("join", Arguments.arena(), MessageKey.HELP_JOIN, Permission.player("join"));
        this.commandGuard = CommandGuard.EXCEPT_NOT_IN_GAME;
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) throws CommandException {
        if (!arena.isSetup()) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_NOT_CONFIGURED);
        }
        arena.addPlayer((Player) sender);
        return CommandResult.success(MessageKey.FEEDBACK_GAME_JOINED);
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}