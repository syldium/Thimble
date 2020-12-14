package me.syldium.thimble.bukkit.util;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.syldium.thimble.common.util.EnumUtil;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class BukkitUtil {

    private static final Constructor<?> FAST_UTIL_OBJECT2INT;

    static {
        Constructor<?> object2Int = null;
        try {
            object2Int = Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap").getConstructor();
        } catch (ReflectiveOperationException ignored) { }
        FAST_UTIL_OBJECT2INT = object2Int;
    }

    private BukkitUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    /**
     * Returns a new {@link Object2IntOpenHashMap}.
     *
     * @param <E> The key type.
     * @return A new fastutil map.
     */
    public static <E> @NotNull Map<E, Integer> newObject2IntMap() {
        if (FAST_UTIL_OBJECT2INT == null) {
            return new Object2IntOpenHashMap<>();
        }

        try {
            // noinspection unchecked
            return (Map<E, Integer>) FAST_UTIL_OBJECT2INT.newInstance();
        } catch (ReflectiveOperationException ex) {
            return new HashMap<>();
        }
    }

    /**
     * Returns a set containing all {@link Material}s that match some patterns.
     *
     * <p>Since Spigot uses ASM to remove the legacy values, {@link EnumUtil#getAllMatching(Class, Predicate)}
     * sees the legacy values, whereas this method does not.</p>
     *
     * @param logger Writes the list of patterns that do not find any material.
     * @param patterns Some patterns.
     * @return All matching Materials.
     */
    public static @NotNull Set<Material> getAllBlocksMatching(@Nullable Logger logger, @NotNull Pattern ...patterns) {
        int[] matched = new int[patterns.length];

        Set<Material> set = EnumSet.noneOf(Material.class);
        for (Material material : Material.values()) {
            for (int i = 0; i < patterns.length; i++) {
                if (patterns[i].matcher(material.name()).matches()) {
                    if (!material.isBlock() || material.isAir()) {
                        if (logger != null) {
                            logger.warning("Skipping " + material.name() + " since it's not a block!");
                        }
                        break;
                    }
                    set.add(material);
                    matched[i] += 1;
                    break;
                }
            }
        }

        if (logger == null) return set;
        for (int i = 0; i < matched.length; i++) {
            if (matched[i] < 1) {
                logger.warning(patterns[i].toString() + " does not match any material!");
            }
        }
        return set;
    }

    /**
     * Returns a set containing all {@link Material}s that match some patterns.
     *
     * <p>{@link #getAllBlocksMatching(Logger, Pattern...)}</p>
     *
     * @param logger Writes the list of patterns that do not find any material.
     * @param stringList A list of regex as strings.
     * @return All matching Materials.
     */
    public static @NotNull Set<Material> getAllBlocksMatching(@Nullable Logger logger, @NotNull List<String> stringList) {
        Pattern[] patterns = stringList.stream()
                .map(Pattern::compile)
                .toArray(Pattern[]::new);
        return getAllBlocksMatching(logger, patterns);
    }
}
