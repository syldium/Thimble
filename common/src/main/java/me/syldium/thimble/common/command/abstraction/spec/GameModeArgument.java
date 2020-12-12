package me.syldium.thimble.common.command.abstraction.spec;

import me.syldium.thimble.api.arena.ThimbleGameMode;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class GameModeArgument extends Argument<ThimbleGameMode> {

    protected GameModeArgument(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull ThimbleGameMode parse(@NotNull ThimblePlugin plugin, @NotNull String given) throws CommandException {
        try {
            return ThimbleGameMode.valueOf(given.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_GAME_MODE_UNKNOWN);
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull ThimblePlugin plugin, @NotNull String given, @NotNull Sender sender) {
        List<String> list = new ArrayList<>(ThimbleGameMode.values().length);
        String uppercase = given.toUpperCase(Locale.ROOT);
        for (ThimbleGameMode gameMode : ThimbleGameMode.values()) {
            if (gameMode.name().startsWith(uppercase)) {
                list.add(gameMode.name().toLowerCase(Locale.ROOT));
            }
        }
        return list;
    }
}
