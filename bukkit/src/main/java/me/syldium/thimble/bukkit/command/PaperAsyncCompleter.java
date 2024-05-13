package me.syldium.thimble.bukkit.command;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.abstraction.Sender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PaperAsyncCompleter extends BukkitCommandExecutor implements Listener {

    public PaperAsyncCompleter(@NotNull ThBukkitPlugin plugin, @NotNull PluginCommand command, @NotNull List<@NotNull String> aliases) {
        super(plugin, command, aliases);
        plugin.registerEvents(this);
    }

    @EventHandler
    public void onAsyncCompletion(AsyncTabCompleteEvent event) {
        if (!event.isCommand() || event.getBuffer().isEmpty()) {
            return;
        }

        List<String> args = this.getArgumentsList(event.getBuffer());
        String label = event.getBuffer().charAt(0) == '/' ? args.get(0).substring(1) : args.get(0);

        if (!label.equals(this.pluginCommand.getLabel()) && !this.pluginCommand.getAliases().contains(label)) {
            return;
        }

        args = args.size() > 1 ? args.subList(1, args.size()) : Collections.emptyList();
        Sender sender = this.plugin.getPlayerAdapter().asAbstractSender(event.getSender());
        event.setCompletions(this.tabCompleteCommand(this.plugin, sender, args));
        event.setHandled(true);
    }
}
