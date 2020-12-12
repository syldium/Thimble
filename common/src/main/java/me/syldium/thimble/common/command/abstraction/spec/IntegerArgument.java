package me.syldium.thimble.common.command.abstraction.spec;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.CommandException.ArgumentParseException;
import me.syldium.thimble.common.player.MessageKey;
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
    public @NotNull Integer parse(@NotNull ThimblePlugin plugin, @NotNull String given) throws ArgumentParseException {
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
