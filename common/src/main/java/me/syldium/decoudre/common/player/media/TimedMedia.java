package me.syldium.decoudre.common.player.media;

import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.player.PlayerAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A media to display a progress to the player.
 */
public interface TimedMedia {

    /**
     * Sets the media audience.
     *
     * @param audience New audience.
     */
    void audience(@NotNull PlayerAudience audience);

    /**
     * Updates the media display.
     *
     * @param percent The percentage covered (from {@code 0.0f} to {@code 1.0f}).
     * @param time The remaining time.
     */
    void progress(float percent, int time);

    /**
     * Hides the progress from the players.
     */
    void hide();

    static @NotNull TimedMedia from(@Nullable PlayerAudience audience, @NotNull MainConfig config, @NotNull String audienceName) {
        if (config.getDisplayType(audienceName) == Type.BOSSBAR) {
            return new BossBarTimedMedia(audience, BossBar.bossBar(Component.empty(), 1.0f, config.getBossBarColor(audienceName), config.getBossBarOverlay(audienceName)));
        }
        return new ExperienceTimedMedia(audience);
    }

    enum Type {
        BOSSBAR,
        EXPERIENCE_BAR
    }
}
