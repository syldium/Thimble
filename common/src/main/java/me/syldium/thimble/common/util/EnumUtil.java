package me.syldium.thimble.common.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Locale;
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

    /**
     * Returns the field matching the provided enum name that exists within the given
     * enum class. If no field is found, this method returns null.
     *
     * @param enumClass The class to search through.
     * @param value The name of a field present or not.
     * @param def The default field.
     * @param <E> The enum to search through.
     * @return The matching enum field.
     */
    @Contract("_, _, !null -> !null")
    public static <E extends Enum<E>> @Nullable E valueOf(@NotNull Class<E> enumClass, @Nullable String value, @Nullable E def) {
        if (value == null) {
            return def;
        }

        try {
            return Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return def;
        }
    }
}
