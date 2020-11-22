package me.syldium.decoudre.common.command.arena;

import me.syldium.decoudre.api.arena.DeArena;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.command.abstraction.spec.Arguments;
import me.syldium.decoudre.common.player.MessageKey;
import me.syldium.decoudre.common.player.Player;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

public class CreateCommand extends ChildCommand.One<String> {

    public CreateCommand() {
        super("create", Arguments.string("name"), MessageKey.HELP_CREATE, Permission.ADMIN);
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull String name) throws CommandException {
        DeArena arena = plugin.getGameService().createArena(name);
        if (arena != null) {
            if (sender instanceof Player) {
                arena.setSpawnLocation(((Player) sender).getLocation());
            }
            return CommandResult.success(MessageKey.FEEDBACK_ARENA_CREATED, Template.of("arena", name));
        }
        return CommandResult.error(MessageKey.FEEDBACK_ARENA_ALREADY_EXISTS, Template.of("arena", name));
    }
}
