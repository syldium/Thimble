package me.syldium.thimble.common.command.arena;

import me.syldium.thimble.api.Location;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

class SetJumpCommand extends ChildCommand.One<Arena> {

    SetJumpCommand() {
        super("setJump", Arguments.arena(), MessageKey.HELP_SET_JUMP, Permission.arenaSetup("set.jump"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) throws CommandException {
        Location loc = ((Player) sender).getLocation();
        arena.setJumpLocation(loc);
        return CommandResult.success(
                MessageKey.FEEDBACK_ARENA_SET_JUMP,
                loc.asPlaceholders()
        );
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
