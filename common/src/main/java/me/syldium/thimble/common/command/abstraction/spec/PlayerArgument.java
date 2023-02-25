package me.syldium.thimble.common.command.abstraction.spec;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PlayerArgument extends Argument<List<Player>> {

    protected PlayerArgument(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull List<Player> parse(@NotNull ThimblePlugin plugin, @NotNull String given) throws CommandException {
        final Player player = plugin.getPlayer(given);
        if (player == null) {
            throw new CommandException(MessageKey.FEEDBACK_GAME_PLAYER_UNKNOWN);
        }
        return Collections.singletonList(player);
    }
}
