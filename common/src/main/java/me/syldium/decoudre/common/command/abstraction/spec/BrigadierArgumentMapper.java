package me.syldium.decoudre.common.command.abstraction.spec;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import org.jetbrains.annotations.NotNull;

public final class BrigadierArgumentMapper {

    private BrigadierArgumentMapper() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @NotNull ArgumentType<?> getArgumentType(@NotNull Argument<?> argument) {
        if (argument instanceof IntegerArgument) {
            return IntegerArgumentType.integer(((IntegerArgument) argument).getMin(), ((IntegerArgument) argument).getMax());
        }
        return StringArgumentType.string();
    }
}
