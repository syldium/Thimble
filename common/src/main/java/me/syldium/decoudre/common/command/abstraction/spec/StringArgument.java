package me.syldium.decoudre.common.command.abstraction.spec;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import org.jetbrains.annotations.NotNull;

class StringArgument extends Argument<String> {

    StringArgument(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull String parse(@NotNull DeCoudrePlugin plugin, @NotNull String given) throws CommandException {
        return given;
    }
}
