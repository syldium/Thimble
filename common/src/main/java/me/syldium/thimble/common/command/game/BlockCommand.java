package me.syldium.thimble.common.command.game;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.abstraction.spec.CommandGuard;
import me.syldium.thimble.common.player.InGamePlayer;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockCommand extends ChildCommand {

    public BlockCommand() {
        super("block", MessageKey.HELP_BLOCK, Permission.player("block"));
        this.commandGuard = CommandGuard.EXCEPT_IN_GAME;
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
        plugin.getPlayerAdapter().openBlockSelectionInventory((Player) sender, (InGamePlayer) plugin.getGameService().player(sender.uuid()).get());
        return CommandResult.success();
    }

    @Override
    public boolean isValidExecutor(@NotNull Sender sender) {
        return sender instanceof Player;
    }
}
