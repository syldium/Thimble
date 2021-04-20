package me.syldium.thimble.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StringUtil {

    private StringUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @NotNull List<@NotNull String> split(@NotNull String string, char delimiter) {
        return split(string, delimiter,5);
    }

    public static @NotNull List<@NotNull String> split(@NotNull String string, char delimiter, int initialListCapacity) {
        if (string.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> result = new ArrayList<>(initialListCapacity);
        int startIndex = 0, foundPosition;
        while ((foundPosition = string.indexOf(delimiter, startIndex)) > -1) {
            result.add(string.substring(startIndex, foundPosition));
            startIndex = foundPosition + 1;
        }
        result.add(string.substring(startIndex));
        return result;
    }

    public static @NotNull String firstToken(@NotNull String string, char delimiter) {
        final int index = string.indexOf(delimiter);
        return index < 0 ? string : string.substring(0, index);
    }
}
