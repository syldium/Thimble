package me.syldium.thimble.common.command.abstraction.spec;

import me.syldium.thimble.api.arena.ThimbleArena;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.game.Arena;
import me.syldium.thimble.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

class ArenaArgument extends Argument<Arena> {

    ArenaArgument(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Arena parse(@NotNull ThimblePlugin plugin, @NotNull String given) throws CommandException {
        return (Arena) plugin.getGameService().getArena(given).orElseThrow(() -> new CommandException(MessageKey.FEEDBACK_GAME_UNKNOWN));
    }

    @Override
    public List<String> tabComplete(@NotNull ThimblePlugin plugin, @NotNull String given, @NotNull Sender sender) {
        return plugin.getGameService().getArenas().stream()
                .map(ThimbleArena::getName)
                .filter(s -> s.startsWith(given))
                .collect(Collectors.toList());
    }
}
