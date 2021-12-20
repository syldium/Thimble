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

import static net.kyori.adventure.text.minimessage.placeholder.Placeholder.component;

class SetMaxCommand extends ChildCommand.Two<Arena, Integer> {

    SetMaxCommand() {
        super("setMax", Arguments.arena(), Arguments.integer("maxPlayers"), null, Permission.arenaSetup("set.max.players"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena, @NotNull Integer maximum) {
        try {
            arena.setMaxPlayers(maximum);
        } catch (IllegalArgumentException ex) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_MAX_LESS_THAN_MIN);
        }
        return CommandResult.success(MessageKey.FEEDBACK_ARENA_SET_MAX, component("max", Component.text(maximum)));
    }
}
