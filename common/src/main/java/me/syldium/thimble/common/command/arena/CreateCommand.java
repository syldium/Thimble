package me.syldium.thimble.common.command.arena;

import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.minimessage.placeholder.Placeholder.miniMessage;

class CreateCommand extends ChildCommand.One<String> {

    CreateCommand() {
        super("create", Arguments.string("name"), MessageKey.HELP_CREATE, Permission.arenaSetup("create"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull String name) throws CommandException {
        ThimbleArena arena = plugin.getGameService().createArena(name);
        if (arena != null) {
            if (sender instanceof Player) {
                arena.setSpawnLocation(((Player) sender).getLocation());
            }
            return CommandResult.success(MessageKey.FEEDBACK_ARENA_CREATED, miniMessage("arena", name));
        }
        return CommandResult.error(MessageKey.FEEDBACK_ARENA_ALREADY_EXISTS, miniMessage("arena", name));
    }
}
