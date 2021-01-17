package me.syldium.thimble.common.util;

import me.syldium.thimble.api.util.PluginVersion;
import org.jetbrains.annotations.NotNull;

public final class MinecraftVersion {

    private static final PluginVersion FLATTENING_VERSION = new PluginVersion(1, 13);
    private static PluginVersion VERSION = new PluginVersion(1, 16, 5);
    private static boolean IS_LEGACY = true;

    private MinecraftVersion() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static void setVersion(@NotNull PluginVersion version) {
        VERSION = version;
        IS_LEGACY = version.compareTo(FLATTENING_VERSION) < 0;
    }

    public static boolean isLegacy() {
        return IS_LEGACY;
    }

    public static @NotNull PluginVersion version() {
        return VERSION;
    }
}
