package me.syldium.thimble.common.command.game;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.Arguments;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AddPointCommand extends ChildCommand.Two<List<Player>, Integer> {

    public AddPointCommand() {
        super("addPoint", Arguments.player(), Arguments.integer("score").optional(), null, Permission.arenaSetup());
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<Player> players, @Nullable Integer scoreToAdd) throws CommandException {
        final int score = scoreToAdd == null ? 1 : scoreToAdd;
        for (Player player : players) {
            final Optional<ThimblePlayer> thimblePlayer = plugin.getGameService().player(player);
            thimblePlayer.ifPresent(tp -> ((InGamePlayer) tp).incrementPoints(score));
        }
        return CommandResult.success();
    }
}
