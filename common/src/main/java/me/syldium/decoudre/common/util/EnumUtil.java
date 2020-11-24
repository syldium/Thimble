package me.syldium.decoudre.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

public final class EnumUtil {

    private EnumUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    /**
     * Returns a set containing all fields of the given enum that match a predicate.
     *
     * @param enumClass The class to search through.
     * @param predicate The predicate that determines if the field is valid.
     * @param <E> The enum to search through.
     * @return All matching enum fields.
     */
    public static <E extends Enum<E>> @NotNull Set<E> getAllMatching(@NotNull Class<E> enumClass, @NotNull Predicate<E> predicate) {
        Set<E> set = EnumSet.noneOf(enumClass);
        for (E field : enumClass.getEnumConstants()) {
            if (predicate.test(field)) {
                set.add(field);
            }
        }
        return set;
    }
}
