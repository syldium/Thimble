package me.syldium.decoudre.common.player.media;

import me.syldium.decoudre.common.player.PlayerAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BossBarTimedMedia implements TimedMedia {

    private @Nullable PlayerAudience audience;
    private final BossBar bossBar;

    BossBarTimedMedia(@Nullable PlayerAudience audience, @NotNull BossBar baseBossBar) {
        this.audience = audience;
        this.bossBar = baseBossBar;
    }

    @Override
    public void audience(@NotNull PlayerAudience audience) {
        this.hide();
        this.audience = audience;
    }

    @Override
    public void progress(float percent, int time) {
        if (this.audience == null) return;
        this.audience.showBossBar(this.bossBar);
        this.bossBar.progress(percent);
        this.bossBar.name(Component.text(time));
    }

    @Override
    public void hide() {
        if (this.audience == null) return;
        this.audience.hideBossBar(this.bossBar);
    }
}
