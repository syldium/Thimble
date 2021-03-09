package me.syldium.thimble.bukkit.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.BrigadierMapper;
import me.syldium.thimble.common.command.abstraction.Sender;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaperCommandExecutor<S extends BukkitBrigadierCommandSource> extends BukkitCommandExecutor implements Listener {

    private final BrigadierMapper<S> brigadierMapper;
    private final CommandMap commandMap;
    private LiteralCommandNode<S> node;

    public PaperCommandExecutor(@NotNull ThBukkitPlugin plugin, @NotNull PluginCommand command, @NotNull List<@NotNull String> aliases) {
        super(plugin, command, aliases);
        this.brigadierMapper = new BrigadierMapper<>(this, (cmd, sender) -> sender.getBukkitSender().hasPermission(cmd.getPermission()));
        this.commandMap = plugin.getBootstrap().getServer().getCommandMap();
        plugin.registerEvents(this);
    }

    @EventHandler
    public void onAsyncCompletion(AsyncTabCompleteEvent event) {
        if (!event.isCommand() || event.getBuffer().isEmpty()) {
            return;
        }

        String[] args = this.getArgumentsArray(event.getBuffer(), -1);
        String label = event.getBuffer().charAt(0) == '/' ? args[0].substring(1) : args[0];

        if (this.commandMap.getCommand(label) != this.pluginCommand) {
            return;
        }

        args = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[]{""};
        Sender sender = this.plugin.getPlayerAdapter().asAbstractSender(event.getSender());
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        event.setCompletions(this.tabCompleteCommand(this.plugin, sender, arguments));
        event.setHandled(true);
    }

    @EventHandler @SuppressWarnings("deprecation")
    public void onCommandRegister(CommandRegisteredEvent<S> event) {
        if (event.getCommandLabel().equals(this.pluginCommand.getLabel())) {
            this.node = this.brigadierMapper.build(event.getLiteral(), event.getBrigadierCommand());
            event.setLiteral(this.node);
        } else if (this.pluginCommand.getAliases().contains(event.getCommandLabel())) {
            if (this.node == null) {
                return; // Alias registered before the main command ?!
            }
            event.setLiteral(this.brigadierMapper.buildRedirect(event.getCommandLabel(), this.node));
        }
    }
}
