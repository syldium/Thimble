package me.syldium.decoudre.bukkit.util;

import me.syldium.decoudre.common.util.EnumUtil;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

public final class BukkitUtil {

    private BukkitUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
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
    public static @NotNull Set<Material> getAllMaterialsMatching(@NotNull Predicate<Material> predicate) {
        Set<Material> set = EnumSet.noneOf(Material.class);
        for (Material field : Material.values()) {
            if (predicate.test(field)) {
                set.add(field);
            }
        }
        return set;
    }
}
