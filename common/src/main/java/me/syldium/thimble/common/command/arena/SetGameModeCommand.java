package me.syldium.thimble.common.command.arena;

import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.minimessage.placeholder.Placeholder.component;

class SetGameModeCommand extends ChildCommand.Two<Arena, ThimbleGameMode> {

    SetGameModeCommand() {
        super("setGameMode", Arguments.arena(), Arguments.gameMode(), MessageKey.HELP_SET_GAME_MODE, Permission.arenaSetup("set.gamemode"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull Arena arena, @NotNull ThimbleGameMode gameMode) throws CommandException {
        arena.setGameMode(gameMode);
        return CommandResult.success(
                MessageKey.FEEDBACK_ARENA_SET_GAME_MODE,
                component("arena", arena.asComponent()),
                component("gamemode", gameMode)
        );
    }
}
