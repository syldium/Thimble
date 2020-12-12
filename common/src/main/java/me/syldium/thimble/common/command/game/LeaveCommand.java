package me.syldium.thimble.common.command.game;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.spec.CommandGuard;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.game.Game;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeaveCommand extends ChildCommand {

    public LeaveCommand() {
        super("leave", MessageKey.HELP_LEAVE, Permission.player("leave"));
        this.commandGuard = CommandGuard.EXCEPT_IN_GAME;
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) {
        Game game = this.getGame(plugin, sender);
        game.removePlayer(sender.uuid());
        return CommandResult.success(MessageKey.FEEDBACK_GAME_LEFT);
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
