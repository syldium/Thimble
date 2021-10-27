package me.syldium.thimble.common.command.arena;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.MessageKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.minimessage.Template.template;

class SetMinCommand extends ChildCommand.Two<Arena, Integer> {

    SetMinCommand() {
        super("setMin", Arguments.arena(), Arguments.integer("minPlayers"), null, Permission.arenaSetup("set.min.players"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena, @NotNull Integer minimum) {
        try {
            arena.setMinPlayers(minimum);
        } catch (IllegalArgumentException ex) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_MIN_GREATER_THAN_MAX);
        }
        return CommandResult.success(MessageKey.FEEDBACK_ARENA_SET_MIN, template("min", Component.text(minimum)));
    }
}
