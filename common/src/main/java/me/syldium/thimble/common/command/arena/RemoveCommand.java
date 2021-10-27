package me.syldium.thimble.common.command.arena;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.minimessage.Template.template;

class RemoveCommand extends ChildCommand.One<Arena> {

    RemoveCommand() {
        super("remove", Arguments.arena(), MessageKey.HELP_REMOVE, Permission.arenaSetup("remove"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena) {
        plugin.getGameService().removeArena(arena);
        return CommandResult.success(MessageKey.FEEDBACK_ARENA_REMOVE, template("arena", arena.name()));
    }
}
