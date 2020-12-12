package me.syldium.thimble.bukkit.util;

import me.syldium.thimble.common.util.EnumUtil;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class BukkitUtil {

    private BukkitUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
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
                    if (!material.isBlock()) {
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
}
