package me.syldium.decoudre.common.command.abstraction.spec;

import me.syldium.decoudre.common.game.Arena;
import org.jetbrains.annotations.NotNull;

/**
 * A set of predefined {@link Argument}s.
 */
public interface Arguments {

    /**
     * Creates a command argument for an {@link Arena}.
     *
     * @return An arena type argument, with the name {@code arena}.
     */
    static @NotNull Argument<Arena> arena() {
        return new ArenaArgument("arena");
    }

    static @NotNull Argument<Integer> integer(@NotNull String name) {
        return new IntegerArgument(name, 1, Integer.MAX_VALUE);
    }

    static @NotNull Argument<Integer> integer(@NotNull String name, int min) {
        return new IntegerArgument(name, min, Integer.MAX_VALUE);
    }

    static @NotNull Argument<Integer> integer(@NotNull String name, int min, int max) {
        return new IntegerArgument(name, min, max);
    }

    /**
     * Creates a {@link String} type command argument.
     *
     * @param name The argument name.
     * @return A string argument.
     */
    static @NotNull Argument<String> string(@NotNull String name) {
        return new StringArgument(name);
    }
}
