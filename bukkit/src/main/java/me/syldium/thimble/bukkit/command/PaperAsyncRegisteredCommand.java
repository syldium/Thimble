package me.syldium.thimble.bukkit.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.BrigadierMapper;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"removal", "deprecation", "UnstableApiUsage"}) // Keep compat for some time
public class PaperAsyncRegisteredCommand<S extends BukkitBrigadierCommandSource> extends PaperAsyncCompleter {

    private final BrigadierMapper<S> brigadierMapper;
    private LiteralCommandNode<S> node;

    public PaperAsyncRegisteredCommand(@NotNull ThBukkitPlugin plugin, @NotNull PluginCommand command, @NotNull List<@NotNull String> aliases) {
        super(plugin, command, aliases);
        this.brigadierMapper = new BrigadierMapper<>(this, (cmd, sender) -> sender.getBukkitSender().hasPermission(cmd.getPermission()));
    }

    @EventHandler
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
