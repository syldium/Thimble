package me.syldium.thimble.common.util;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.arena.ThimbleSingleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.api.util.Leaderboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Queue;
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

    public static @Nullable UUID nextJumper(@NotNull ThimblePlayer player, int index) {
        ThimbleGame game = player.game();
        if (game instanceof ThimbleSingleGame) {
            Queue<UUID> queue = ((ThimbleSingleGame) game).jumperQueue();
            if (index < 1) {
                return queue.peek();
            }

            int i = 0;
            Iterator<UUID> iterator = queue.iterator();
            while (i++ < index && iterator.hasNext()) {
                iterator.next();
            }
            return iterator.hasNext() ? iterator.next() : null;
        }
        return null;
    }

    public static @Nullable UUID topPlayer(@NotNull ThimblePlayer player, int index) {
        Leaderboard<ThimblePlayer> leaderboard = player.game().leaderboard();
        if (index < leaderboard.size()) {
            return leaderboard.get(index).uuid();
        }
        return null;
    }

    public static @Nullable Integer topPoints(@NotNull ThimblePlayer player, int index) {
        Leaderboard<ThimblePlayer> leaderboard = player.game().leaderboard();
        if (index < leaderboard.size()) {
            return leaderboard.get(index).points();
        }
        return null;
    }
}
