package me.syldium.decoudre.common.config;

import me.syldium.decoudre.common.player.media.TimedMedia;
import me.syldium.decoudre.common.service.DataService;
import me.syldium.decoudre.common.util.EnumUtil;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public interface MainConfig {

    /**
     * Gets the locale to display messages.
     *
     * @return The locale.
     */
    default @NotNull Locale getLocale() {
        return Locale.getDefault();
    }

    default @NotNull List<@NotNull String> getEnabledIntegrations() {
        return Collections.emptyList();
    }

    // === Database ===

    /**
     * Gets the save method chosen for the statistics.
     *
     * @return The storage method.
     */
    @NotNull DataService.Type getDataStorageMethod();

    /**
     * Gets the JDBC url to interact with the database.
     *
     * @return The connection url.
     */
    @NotNull String getJdbcUrl();

    @Nullable String getJdbcUsername();

    @Nullable String getJdbcPassword();

    /**
     * Returns if players should be teleported to the arena spawn at the end of a game.
     *
     * @return If they should be teleported.
     */
    boolean doesTeleportAtEnd();

    /**
     * Returns if failed jumps cost points in a {@link me.syldium.decoudre.common.game.ConcurrentGame}.
     *
     * @return If any points should be removed.
     */
    boolean doesCountFailsInConcurrent();

    /**
     * Returns the duration in seconds of the countdown before the game starts.
     *
     * @return Duration in seconds.
     */
    int getCountdownTime();

    /**
     * Returns the time in seconds that the player has to prepare his jump in single mode.
     *
     * @return Duration in seconds.
     */
    int getJumpTimeSingleMode();

    /**
     * Returns the time in seconds that the player has to prepare his jump in concurrent mode.
     *
     * @return Duration in seconds.
     */
    int getJumpTimeConcurrentMode();

    // === Display ===

    @Nullable String getRawDisplayProperty(@NotNull String audienceName, @NotNull String propertyKey);

    /**
     * Returns the display type of the time counters. {@link #getRawDisplayProperty(String, String)}
     *
     * @param audienceName The audience name (global/jump).
     * @return The display type.
     */
    default @NotNull TimedMedia.Type getDisplayType(@NotNull String audienceName) {
        return EnumUtil.valueOf(TimedMedia.Type.class, this.getRawDisplayProperty(audienceName, "type"), TimedMedia.Type.BOSSBAR);
    }

    /**
     * Returns the overlay of a {@link BossBar}. {@link #getRawDisplayProperty(String, String)}
     *
     * @param audienceName The audience name (global/jump).
     * @return The boss bar overlay.
     */
    default @NotNull BossBar.Overlay getBossBarOverlay(@NotNull String audienceName) {
        return EnumUtil.valueOf(BossBar.Overlay.class, this.getRawDisplayProperty(audienceName, "bossbar-overlay"), BossBar.Overlay.PROGRESS);
    }

    /**
     * Returns the color of a {@link BossBar}. {@link #getRawDisplayProperty(String, String)}
     *
     * @param audienceName The audience name (global/jump).
     * @return The boss bar color.
     */
    default @NotNull BossBar.Color getBossBarColor(@NotNull String audienceName) {
        return EnumUtil.valueOf(BossBar.Color.class, this.getRawDisplayProperty(audienceName, "bossbar-color"), "global".equals(audienceName) ? BossBar.Color.YELLOW : BossBar.Color.RED);
    }

    default double getWinnerDeposit() {
        return 5.0D;
    }
}
