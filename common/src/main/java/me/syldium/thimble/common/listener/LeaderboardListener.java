package me.syldium.thimble.common.listener;

import me.syldium.thimble.api.player.ThimblePlayer;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LeaderboardListener {

    void onPointsUpdated(@NotNull ThimblePlayer player);
}
