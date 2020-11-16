package me.syldium.decoudre.common.command.abstraction.spec;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.CommandException.ArgumentParseException;
import me.syldium.decoudre.common.player.Message;
import org.jetbrains.annotations.NotNull;

class IntegerArgument extends Argument<Integer> {

    private final int min;
    private final int max;

    IntegerArgument(@NotNull String name, int min, int max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull Integer parse(@NotNull DeCoudrePlugin plugin, @NotNull String given) throws ArgumentParseException {
        try {
            int n = Integer.parseInt(given);
            if (n < this.min || n > this.max) {
                throw new ArgumentParseException(Message.NAN);
            }
            return n;
        } catch (NumberFormatException e){
            throw new ArgumentParseException(Message.NAN);
        }
    }

    @Override
    public @NotNull IntegerArgumentType asBrigadierType() {
        return IntegerArgumentType.integer(this.min, this.max);
    }
}
