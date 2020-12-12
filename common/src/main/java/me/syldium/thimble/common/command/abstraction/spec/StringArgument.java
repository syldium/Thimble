package me.syldium.thimble.common.command.abstraction.spec;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.CommandException;
import org.jetbrains.annotations.NotNull;

class StringArgument extends Argument<String> {

    StringArgument(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull String parse(@NotNull ThimblePlugin plugin, @NotNull String given) throws CommandException {
        return given;
    }
}
