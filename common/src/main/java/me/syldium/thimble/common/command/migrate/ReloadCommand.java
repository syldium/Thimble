package me.syldium.thimble.common.command.migrate;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadCommand extends ChildCommand {

    public ReloadCommand() {
        super("reload", MessageKey.HELP_RELOAD, Permission.reload());
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) {
        plugin.getConfigManager().reload();
        return CommandResult.success(MessageKey.FEEDBACK_RELOAD);
    }
}
