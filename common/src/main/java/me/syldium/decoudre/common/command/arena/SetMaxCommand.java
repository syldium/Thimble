package me.syldium.decoudre.common.command.arena;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.command.abstraction.spec.Arguments;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.common.player.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

class SetMaxCommand extends ChildCommand.Two<Arena, Integer> {

    SetMaxCommand() {
        super("setMax", Arguments.arena(), Arguments.integer("maxPlayers"), null, Permission.arenaSetup("set.max.players"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull Arena arena, @NotNull Integer maximum) {
        try {
            arena.setMaxPlayers(maximum);
        } catch (IllegalArgumentException ex) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_MAX_LESS_THAN_MIN);
        }
        return CommandResult.success(MessageKey.FEEDBACK_ARENA_SET_MAX, Template.of("max", Component.text(maximum)));
    }
}
