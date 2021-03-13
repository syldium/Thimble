package me.syldium.thimble.common.util;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleSingleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class PlaceholderUtil {

    private PlaceholderUtil() throws IllegalAccessException {
        throw new IllegalAccessException(this.getClass().getSimpleName() + " cannot be instantiated.");
    }

    public static @Nullable UUID currentJumper(@NotNull ThimblePlayer player) {
        ThimbleGame game = player.game();
        if (game instanceof ThimbleSingleGame) {
            return ((ThimbleSingleGame) game).currentJumper();
        }
        return null;
    }
}
