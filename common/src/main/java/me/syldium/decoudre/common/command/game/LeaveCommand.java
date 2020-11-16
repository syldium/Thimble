package me.syldium.decoudre.common.command.game;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.spec.CommandGuard;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.game.Game;
import me.syldium.decoudre.common.player.Message;
import me.syldium.decoudre.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeaveCommand extends ChildCommand {

    public LeaveCommand() {
        super("leave", Message.LEAVE_ARENA, Permission.PLAYER);
        this.commandGuard = CommandGuard.EXCEPT_IN_GAME;
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) {
        Game game = this.getGame(plugin, (Player) sender);
        game.removePlayer(sender.getUuid());
        return CommandResult.SUCCESS;
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
