package me.syldium.thimble.api;

import me.syldium.thimble.api.service.GameService;
import me.syldium.thimble.api.service.StatsService;
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
            throw new IllegalStateException("The game service is not loaded.");
        }
        return STATS_SERVICE;
    }

    /**
     * Gets the current implementation version string.
     *
     * @return The implementation version.
     */
    public static @NotNull String getImplVersion() {
        return Thimble.class.getPackage().getImplementationVersion();
    }

    /**
     * Is the requested version is greater than or equal to the current version.
     *
     * @param major The major version number.
     * @param minor The minor number.
     * @param patch The patch number.
     * @return {@code true} if the version is correct.
     */
    public static boolean isVersion(int major, int minor, int patch) {
        return PLUGIN_VERSION.compareTo(new PluginVersion(major, minor, patch)) >= 0;
    }

    /**
     * Runs the code if the requested version is greater than or equal to the current version.
     *
     * @param major The major version number.
     * @param minor The minor number.
     * @param patch The patch number.
     * @param runnable The code if the version is satisfied.
     */
    public static void ifVersion(int major, int minor, int patch, @NotNull Runnable runnable) {
        requireNonNull(runnable, "runnable");
        if (isVersion(major, minor, patch)) {
            runnable.run();
        }
    }

    /**
     * Runs the code if the requested version is less than or equal to the current version.
     *
     * @param major The major version number.
     * @param minor The minor number.
     * @param patch The patch number.
     * @param supplier The code if the version is satisfied.
     * @param <T> The return type.
     * @return The return, or nothing.
     */
    public static <T> @NotNull Optional<T> ifVersion(int major, int minor, int patch, @NotNull Supplier<T> supplier) {
        requireNonNull(supplier, "supplier");
        return isVersion(major, minor, patch) ? Optional.of(supplier.get()) : Optional.empty();
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
