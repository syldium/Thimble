package me.syldium.decoudre.common.command.abstraction.spec;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.syldium.decoudre.api.arena.DeArena;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.game.Arena;
import me.syldium.decoudre.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

class ArenaArgument extends Argument<Arena> {

    ArenaArgument(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Arena parse(@NotNull DeCoudrePlugin plugin, @NotNull String given) throws CommandException {
        return (Arena) plugin.getGameService().getArena(given).orElseThrow(() -> new CommandException(MessageKey.FEEDBACK_GAME_UNKNOWN));
    }

    @Override
    public List<String> tabComplete(@NotNull DeCoudrePlugin plugin, @NotNull String given, @NotNull Sender sender) {
        return plugin.getGameService().getArenas().stream()
                .filter(arena -> arena.getName().startsWith(given))
                .map(DeArena::getName)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull StringArgumentType asBrigadierType() {
        return StringArgumentType.string();
    }
}
