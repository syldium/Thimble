package me.syldium.thimble.common.util;

import me.syldium.thimble.api.arena.ThimbleGame;
import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public enum SignAction {

    BLOCK {
        @Override
        public void run(@NotNull ThimblePlugin plugin, @NotNull Player player) {
            Optional<ThimblePlayer> thimblePlayer = plugin.getGameService().getInGamePlayer(player);
            if (thimblePlayer.isPresent()) {
                plugin.getPlayerAdapter().openBlockSelectionInventory(player, (InGamePlayer) thimblePlayer.get());
            } else {
                player.sendFeedback(CommandResult.error(MessageKey.FEEDBACK_GAME_NOT_IN_GAME));
            }
        }
    },
    LEAVE {
        @Override
        public void run(@NotNull ThimblePlugin plugin, @NotNull Player player) {
            Optional<ThimbleGame> thimbleGame = plugin.getGameService().getGame(player);
            if (thimbleGame.isPresent()) {
                thimbleGame.get().removePlayer(player);
            } else {
                player.sendFeedback(CommandResult.error(MessageKey.FEEDBACK_GAME_NOT_IN_GAME));
            }
        }
    };

    public abstract void run(@NotNull ThimblePlugin plugin, @NotNull Player player);
}
