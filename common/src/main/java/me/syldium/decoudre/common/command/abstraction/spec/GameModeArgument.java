package me.syldium.decoudre.common.command.abstraction.spec;

import me.syldium.decoudre.api.arena.DeGameMode;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class GameModeArgument extends Argument<DeGameMode> {

    protected GameModeArgument(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull DeGameMode parse(@NotNull DeCoudrePlugin plugin, @NotNull String given) throws CommandException {
        try {
            return DeGameMode.valueOf(given.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_GAME_MODE_UNKNOWN);
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull DeCoudrePlugin plugin, @NotNull String given, @NotNull Sender sender) {
        List<String> list = new ArrayList<>(DeGameMode.values().length);
        String uppercase = given.toUpperCase(Locale.ROOT);
        for (DeGameMode gameMode : DeGameMode.values()) {
            if (gameMode.name().startsWith(uppercase)) {
                list.add(gameMode.name().toLowerCase(Locale.ROOT));
            }
        }
        return list;
    }
}
