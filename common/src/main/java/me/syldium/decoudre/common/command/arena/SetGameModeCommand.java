package me.syldium.decoudre.common.command.arena;

import me.syldium.decoudre.api.arena.DeGameMode;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.command.abstraction.spec.Arguments;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.common.player.MessageKey;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

class SetGameModeCommand extends ChildCommand.Two<Arena, DeGameMode> {

    SetGameModeCommand() {
        super("setGameMode", Arguments.arena(), Arguments.gameMode(), MessageKey.HELP_SET_GAME_MODE, Permission.arenaSetup("set.gamemode"));
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull Arena arena, @NotNull DeGameMode gameMode) throws CommandException {
        arena.setGameMode(gameMode);
        return CommandResult.success(
                MessageKey.FEEDBACK_ARENA_SET_GAME_MODE,
                Template.of("arena", arena.asComponent()),
                Template.of("gamemode", gameMode.name().toLowerCase(Locale.ROOT))
        );
    }
}
