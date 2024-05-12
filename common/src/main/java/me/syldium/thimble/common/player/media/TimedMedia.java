package me.syldium.thimble.common.player.media;

import me.syldium.thimble.common.config.MainConfig;
import me.syldium.thimble.common.player.PlayerAudience;
import me.syldium.thimble.common.service.MessageService;
import me.syldium.thimble.common.util.Task;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

/**
 * A media to display a progress to the player.
 */
public interface TimedMedia {

    /**
     * Displays a progress.
     *
     * @param audience The audience.
     * @param progress The progress covered (from {@code 0.0f} to {@code 1.0f}).
     * @param time The remaining time.
     */
    void progress(@NotNull PlayerAudience audience, float progress, int time);

    /**
     * Displays a progress from ticks.
     *
     * @param audience The audience.
     * @param ticks The amount of ticks elapsed.
     * @param total The total number of ticks.
     */
    default void progress(@NotNull PlayerAudience audience, int ticks, int total) {
        this.progress(audience, (float) ticks / total, ticksToSeconds(ticks));
    }

    /**
     * Hides the progress from the players.
     *
     * @param audience The audience.
     */
    void hide(@NotNull PlayerAudience audience);

    static @NotNull TimedMedia from(@NotNull MainConfig config, @NotNull MessageService service, @NotNull String audienceName) {
        if (config.getDisplayType(audienceName) == Type.BOSSBAR) {
            return new BossBarTimedMedia(
                    BossBar.bossBar(Component.empty(), 1.0f, config.getBossBarColor(audienceName), config.getBossBarOverlay(audienceName)),
                    service,
                    config.getTimedMessage(audienceName)
            );
        }
        return new ExperienceTimedMedia();
    }

    static int ticksToSeconds(int ticks) {
        return (int) Math.ceil((float) ticks / Task.GAME_TICKS_PER_SECOND);
    }

    enum Type {
        BOSSBAR("bossbar"),
        EXPERIENCE_BAR("experience-bar");

        public static final Index<String, Type> NAMES = Index.create(Type.class, mode -> mode.name);
        private final String name;

        Type(@NotNull String name) {
            this.name = name;
        }
    }
}
