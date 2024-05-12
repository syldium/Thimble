package me.syldium.thimble.common.player.media;

import me.syldium.thimble.common.player.MessageRange;
import me.syldium.thimble.common.player.PlayerAudience;
import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;

final class BossBarTimedMedia implements TimedMedia {

    private final BossBar bossBar;
    private final MessageService service;
    private final MessageRange keys;
    private int time = 0;

    BossBarTimedMedia(@NotNull BossBar baseBossBar, @NotNull MessageService service, @NotNull MessageRange keys) {
        this.bossBar = baseBossBar;
        this.service = service;
        this.keys = keys;
    }

    @Override
    public void progress(@NotNull PlayerAudience audience, float progress, int time) {
        this.bossBar.progress(progress);
        if (this.time != time) {
            this.bossBar.name(this.service.formatMessage(
                    this.keys.getMessage(time),
                    component("time", text(time)),
                    component("seconds", text(time % 60)),
                    component("minutes", text(time / 60)))
            );
            this.time = time;
        }
        audience.showBossBar(this.bossBar);
    }

    @Override
    public void hide(@NotNull PlayerAudience audience) {
        audience.hideBossBar(this.bossBar);
    }
}
