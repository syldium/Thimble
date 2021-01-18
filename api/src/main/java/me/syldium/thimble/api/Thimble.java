package me.syldium.thimble.api;

import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.api.service.StatsService;
import me.syldium.thimble.api.util.PluginVersion;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Entry point for the Thimble plugin api.
 */
@SuppressWarnings("unused")
public final class Thimble {

    private static final PluginVersion PLUGIN_VERSION = new PluginVersion(getImplVersion());
    private static GameService GAME_SERVICE;
    private static StatsService STATS_SERVICE;

    private Thimble() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    /**
     * Gets the {@link GameService}.
     *
     * @return The game service.
     * @throws IllegalStateException If it is not loaded.
     */
    public static @NotNull GameService getGameService() {
        if (GAME_SERVICE == null) {
            throw new IllegalStateException("The game service is not loaded.");
        }
        return GAME_SERVICE;
    }

    /**
     * Gets the {@link StatsService}.
     *
     * @return The statistics service.
     * @throws IllegalStateException If it is not loaded.
     */
    public static @NotNull StatsService getStatsService() {
        if (STATS_SERVICE == null) {
            throw new IllegalStateException("The stats service is not loaded.");
        }
        return STATS_SERVICE;
    }

    /**
     * Gets the current implementation version string.
     *
     * @return The implementation version.
     */
    public static @NotNull @Subst("1.0") String getImplVersion() {
        return Thimble.class.getPackage().getImplementationVersion();
    }

    /**
     * Gets the currently used version of the plugin.
     *
     * @return The plugin version instance.
     */
    public static @NotNull PluginVersion pluginVersion() {
        return PLUGIN_VERSION;
    }

    /**
     * Is the requested version is greater than or equal to the current version.
     *
     * @param version The version number.
     * @return {@code true} if the version is correct.
     */
    public static boolean isVersion(int... version) {
        return PLUGIN_VERSION.compareTo(new PluginVersion(version)) >= 0;
    }

    /**
     * Runs the code if the requested version is greater than or equal to the current version.
     *
     * @param runnable The code if the version is satisfied.
     * @param version The version number.
     */
    public static void ifVersion(@NotNull Runnable runnable, int... version) {
        requireNonNull(runnable, "runnable");
        if (isVersion(version)) {
            runnable.run();
        }
    }

    /**
     * Runs the code if the requested version is less than or equal to the current version.
     *
     * @param supplier The code if the version is satisfied.
     * @param version The version number.
     * @param <T> The return type.
     * @return The return, or nothing.
     */
    public static <T> @NotNull Optional<T> ifVersion(@NotNull Supplier<T> supplier, int... version) {
        requireNonNull(supplier, "supplier");
        return isVersion(version) ? Optional.of(supplier.get()) : Optional.empty();
    }

    @ApiStatus.Internal
    public static void setGameService(@Nullable GameService service) {
        GAME_SERVICE = service;
    }

    @ApiStatus.Internal
    public static void setStatsService(@Nullable StatsService service) {
        STATS_SERVICE = service;
    }
}
