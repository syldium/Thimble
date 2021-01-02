package me.syldium.thimble.common.player;

import net.kyori.adventure.audience.Audience;

public interface PlayerAudience extends Audience {

    /**
     * Sends a (fake) experience change.
     *
     * @param percent New experience progress percentage (from {@code 0.0f} to {@code 1.0f}).
     * @param level New experience level.
     */
    void sendExperienceChange(float percent, int level);
}
