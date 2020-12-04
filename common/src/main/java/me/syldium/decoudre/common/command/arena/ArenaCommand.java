package me.syldium.decoudre.common.command.arena;

import me.syldium.decoudre.common.command.abstraction.ParentCommand;
import me.syldium.decoudre.common.command.abstraction.Permission;

import java.util.Arrays;

public class ArenaCommand extends ParentCommand {

    public ArenaCommand() {
        super("arena", Arrays.asList(
                new CreateCommand(),
                new SetMaxCommand(),
                new SetMinCommand(),
                new SetSpawnCommand(),
                new SetJumpCommand()
        ), null, Permission.arenaSetup());
    }
}
