package me.syldium.decoudre.common.command.abstraction.spec;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.CommandException.ArgumentParseException;
import me.syldium.decoudre.common.player.MessageKey;
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
                throw new ArgumentParseException(MessageKey.FEEDBACK_NAN);
            }
            return n;
        } catch (NumberFormatException e){
            throw new ArgumentParseException(MessageKey.FEEDBACK_NAN);
        }
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }
}
