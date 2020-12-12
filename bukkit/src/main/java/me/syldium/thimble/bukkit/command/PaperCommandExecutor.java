package me.syldium.thimble.bukkit.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.BrigadierMapper;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class PaperCommandExecutor<S extends BukkitBrigadierCommandSource> extends BukkitCommandExecutor implements Listener {

    private final BrigadierMapper<S> brigadierMapper;

    public PaperCommandExecutor(@NotNull ThBukkitPlugin plugin, @NotNull PluginCommand command) {
        super(plugin, command);
        this.brigadierMapper = new BrigadierMapper<>(this, (cmd, sender) -> sender.getBukkitSender().hasPermission(cmd.getPermission()));
        plugin.registerEvents(this);
    }

    @EventHandler
    public void onAsyncCompletion(AsyncTabCompleteEvent event) {
        if (!event.isCommand() || event.getBuffer().isEmpty()) {
            return;
        }

        String[] args = this.getArgumentsArray(event.getBuffer(), -1);
        String label = event.getBuffer().charAt(0) == '/' ? args[0].substring(1) : args[0];
        args = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[]{""};

        if (!label.equals(this.pluginCommand.getLabel())) {
            return;
        }

        event.setCompletions(this.tabCompleteCommand(this.plugin, this.plugin.getPlayerAdapter().asAbstractSender(event.getSender()), new ArrayList<>(Arrays.asList(args))));
        event.setHandled(true);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onCommandRegister(CommandRegisteredEvent<S> event) {
        if (event.getCommandLabel().equals(this.pluginCommand.getLabel())) {
            event.setLiteral(this.brigadierMapper.build(event.getLiteral(), event.getBrigadierCommand()));
        }
    }
}
