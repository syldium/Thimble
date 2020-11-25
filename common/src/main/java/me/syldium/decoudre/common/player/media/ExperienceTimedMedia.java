package me.syldium.decoudre.common.player.media;

import me.syldium.decoudre.common.player.PlayerAudience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ExperienceTimedMedia implements TimedMedia {

    private @Nullable PlayerAudience audience;

    ExperienceTimedMedia(@Nullable PlayerAudience audience) {
        this.audience = audience;
    }

    @Override
    public void audience(@NotNull PlayerAudience audience) {
        this.hide();
        this.audience = audience;
    }

    @Override
    public void progress(float percent, int time) {
        if (this.audience == null) return;
        this.audience.sendExperienceChange(percent, time);
    }

    @Override
    public void hide() {
        if (this.audience == null) return;
        this.audience.sendRealExperience();
    }
}
