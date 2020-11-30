package me.syldium.decoudre.common.player.media;

import me.syldium.decoudre.common.config.MainConfig;
import me.syldium.decoudre.common.player.PlayerAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A media to display a progress to the player.
 */
public interface TimedMedia {

    /**
     * Displays a progress.
     *
     * @param audience The audience.
     * @param percent The percentage covered (from {@code 0.0f} to {@code 1.0f}).
     * @param time The remaining time.
     */
    void progress(@NotNull PlayerAudience audience, float percent, int time);

    /**
     * Hides the progress from the players.
     *
     * @param audience The audience.
     */
    void hide(@NotNull PlayerAudience audience);

    static @NotNull TimedMedia from(@NotNull MainConfig config, @NotNull String audienceName) {
        if (config.getDisplayType(audienceName) == Type.BOSSBAR) {
            return new BossBarTimedMedia(BossBar.bossBar(Component.empty(), 1.0f, config.getBossBarColor(audienceName), config.getBossBarOverlay(audienceName)));
        }
        return new ExperienceTimedMedia();
    }

    enum Type {
        BOSSBAR,
        EXPERIENCE_BAR
    }
}
