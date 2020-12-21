package me.syldium.thimble.common.command.arena;

import me.syldium.thimble.common.command.abstraction.ParentCommand;
import me.syldium.thimble.common.command.abstraction.Permission;

import java.util.ArrayList;
import java.util.Arrays;

public class ArenaCommand extends ParentCommand {

    public ArenaCommand() {
        super("arena", new ArrayList<>(Arrays.asList(
                new CreateCommand(),
                new SetMaxCommand(),
                new SetMinCommand(),
                new SetGameModeCommand(),
                new SetSpawnCommand(),
                new SetJumpCommand(),
                new SetWaitCommand(),
                new RemoveCommand()
        )), null, Permission.arenaSetup());
    }
}
