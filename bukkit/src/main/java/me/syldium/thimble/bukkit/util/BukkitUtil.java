package me.syldium.thimble.bukkit.util;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.syldium.thimble.common.util.EnumUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
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

    private static final Set<Material> AIR_TYPES = getAllMatching(material -> material.name().endsWith("AIR"));

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
     * Returns a set containing all {@link Material}s that match a predicate.
     *
     * <p>Since Spigot uses ASM to remove the legacy values, {@link EnumUtil#getAllMatching(Class, Predicate)}
     * sees the legacy values, whereas this method does not.</p>
     *
     * @param predicate The predicate that determines if the Material is valid.
     * @return All matching Materials.
     */
    public static @NotNull Set<Material> getAllMatching(@NotNull Predicate<Material> predicate) {
        Set<Material> set = EnumSet.noneOf(Material.class);
        for (Material material : Material.values()) {
            if (predicate.test(material)) {
                set.add(material);
            }
        }
        return set;
    }

    /**
     * Returns a set containing all {@link Material}s that match some patterns.
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
                    if (!material.isBlock() || isAir(material)) {
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

    /**
     * Checks if this block is or contains liquid.
     *
     * @param block A block.
     * @return {@code true} if this block is/contains liquid.
     */
    public static boolean containsLiquid(@NotNull Block block) {
        if (block.isLiquid()) {
            return true;
        }
        if (block.isEmpty()) {
            return false;
        }

        BlockData blockData = block.getBlockData();
        if (blockData instanceof Waterlogged) {
            return ((Waterlogged) blockData).isWaterlogged();
        }
        return false;
    }

    /**
     * Checks if the {@link Material} is an air block without using {@link Material#isAir()}.
     *
     * @param material The Material to test.
     * @return {@code true} if it's an air block.
     */
    public static boolean isAir(@NotNull Material material) {
        return AIR_TYPES.contains(material);
    }
}
