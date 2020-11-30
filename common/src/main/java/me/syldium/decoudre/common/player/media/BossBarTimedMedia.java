package me.syldium.decoudre.common.player.media;

import me.syldium.decoudre.common.player.PlayerAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

final class BossBarTimedMedia implements TimedMedia {

    private final BossBar bossBar;

    BossBarTimedMedia(@NotNull BossBar baseBossBar) {
        this.bossBar = baseBossBar;
    }

    @Override
    public void progress(@NotNull PlayerAudience audience, float percent, int time) {
        this.bossBar.progress(percent);
        this.bossBar.name(Component.text(time));
        audience.showBossBar(this.bossBar);
    }

    @Override
    public void hide(@NotNull PlayerAudience audience) {
        audience.hideBossBar(this.bossBar);
    }
}
