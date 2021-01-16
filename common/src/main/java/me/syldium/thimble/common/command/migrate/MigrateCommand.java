package me.syldium.thimble.common.command.migrate;

import me.syldium.thimble.common.command.abstraction.ParentCommand;
import me.syldium.thimble.common.command.abstraction.Permission;
import me.syldium.thimble.common.player.MessageKey;

import java.util.LinkedList;

public class MigrateCommand extends ParentCommand {

    public MigrateCommand() {
        super("migrate", new LinkedList<>(), MessageKey.HELP_MIGRATE, Permission.migrate());
    }
}
