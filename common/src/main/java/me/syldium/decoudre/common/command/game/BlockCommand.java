package me.syldium.decoudre.common.command.game;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.ChildCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.abstraction.Permission;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.command.abstraction.spec.CommandGuard;
import me.syldium.decoudre.common.player.InGamePlayer;
import me.syldium.decoudre.common.player.MessageKey;
import me.syldium.decoudre.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockCommand extends ChildCommand {

    public BlockCommand() {
        super("block", MessageKey.HELP_BLOCK, Permission.PLAYER);
        this.commandGuard = CommandGuard.EXCEPT_IN_UNSTARTED_GAME;
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
        plugin.getPlayerAdapter().openBlockSelectionInventory((Player) sender, (InGamePlayer) plugin.getGameService().getInGamePlayer(sender.uuid()).get());
        return CommandResult.success();
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
