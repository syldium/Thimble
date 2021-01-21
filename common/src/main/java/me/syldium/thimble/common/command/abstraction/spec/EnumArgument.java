package me.syldium.thimble.common.command.abstraction.spec;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class EnumArgument<E extends Enum<E>> extends Argument<E> {

    private final Class<E> enumClass;

    protected EnumArgument(@NotNull Class<E> enumClass, @NotNull String name) {
        super(name);
        this.enumClass = enumClass;
    }

    @Override
    public @NotNull E parse(@NotNull ThimblePlugin plugin, @NotNull String given) throws CommandException {
        try {
            return Enum.valueOf(this.enumClass, given.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CommandException(MessageKey.FEEDBACK_ARENA_SET_GAME_MODE_UNKNOWN);
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull ThimblePlugin plugin, @NotNull String given, @NotNull Sender sender) {
        E[] values = this.enumClass.getEnumConstants();
        List<String> list = new ArrayList<>(Math.min(10, values.length));
        String uppercase = given.toUpperCase(Locale.ROOT);
        for (E value : values) {
            if (value.name().startsWith(uppercase)) {
                list.add(value.name().toLowerCase(Locale.ROOT));
            }
        }
        return list;
    }
}
