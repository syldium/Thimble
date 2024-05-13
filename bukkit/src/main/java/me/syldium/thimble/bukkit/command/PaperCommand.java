package me.syldium.thimble.bukkit.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.syldium.thimble.bukkit.ThBukkitPlugin;
import me.syldium.thimble.common.command.BrigadierMapper;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PaperCommand extends PaperAsyncCompleter {

    private final BrigadierMapper<CommandSourceStack> brigadierMapper;
    private SuggestionProvider<CommandSourceStack> suggestionProvider;

    public PaperCommand(@NotNull ThBukkitPlugin plugin, @NotNull PluginCommand command, @NotNull List<@NotNull String> aliases) {
        super(plugin, command, Collections.emptyList()); // Do not add aliases here, because those would not be overridden
        command.getAliases().addAll(aliases);
        this.brigadierMapper = new BrigadierMapper<>(this, (cmd, sender) -> sender.getSender().hasPermission(cmd.getPermission()));
        final LifecycleEventManager<Plugin> manager = plugin.getBootstrap().getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            CommandNode<CommandSourceStack> old = commands.getDispatcher().getRoot().getChild(command.getName());
            if (old != null) {
                old = old.getChildren().iterator().next();
            }
            if (old instanceof ArgumentCommandNode) {
                this.suggestionProvider = ((ArgumentCommandNode<CommandSourceStack, String>) old).getCustomSuggestions();
            }
            LiteralCommandNode<CommandSourceStack> literal = Commands.literal(command.getName())
                    .executes(new RawExecutor(command))
                    .build();
            literal = this.brigadierMapper.build(literal, this.suggestionProvider);
            commands.register(literal, aliases);
        });
    }

    private static final class RawExecutor implements Command<CommandSourceStack> {

        private final org.bukkit.command.Command inner;

        private RawExecutor(org.bukkit.command.Command inner) {
            this.inner = inner;
        }

        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            final CommandSender sender = context.getSource().getSender();
            final String content = context.getRange().get(context.getInput());
            final String[] args = org.apache.commons.lang3.StringUtils.split(content, ' ');
            return this.inner.execute(sender, this.inner.getName(), args) ? 1 : 0;
        }
    }
}
