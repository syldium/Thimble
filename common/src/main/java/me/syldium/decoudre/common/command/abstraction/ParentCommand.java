package me.syldium.decoudre.common.command.abstraction;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.player.Message;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParentCommand extends AbstractCommand {

    private final List<? extends AbstractCommand> children;
    private final Component usage;

    public ParentCommand(String name, List<? extends AbstractCommand> children, Message description, Permission permission) {
        super(name, description, permission);
        this.children = children;
        this.usage = Component.text(name);
        children.forEach(c -> c.parent = this);
    }

    @Override
    public @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
        if (args.size() > 0 && !args.get(0).equalsIgnoreCase("help")) {
            throw new CommandException(Message.UNKNOWN_COMMAND);
        }

        this.children.stream()
                .filter(s -> s.hasPermission(sender))
                .forEach(s -> sender.sendMessage(s.getHelp()));
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull List<@NotNull String> tabComplete(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) {
        if (args.size() < 1) {
            return Collections.emptyList();
        }
        List<String> complete = this.children.stream()
                .filter(cmd -> cmd.getName().toLowerCase().startsWith(args.get(0).toLowerCase()))
                .filter(cmd -> cmd.hasPermission(sender))
                .map(AbstractCommand::getName)
                .collect(Collectors.toList());

        if ("help".startsWith(args.get(0).toLowerCase())) {
            complete.add("help");
        }
        return complete;
    }

    @Override
    public @NotNull Component getHelp() {
        return this.usage;
    }

    public @NotNull Component getUsage() {
        return this.usage;
    }

    @Override
    public int getMinArgumentCount() {
        return 0;
    }

    @Override
    public @NotNull AbstractCommand get(@NotNull List<@NotNull String> args) {
        if (args.size() == 0) {
            return this;
        }
        List<AbstractCommand> sub = this.children.stream()
                .filter(AbstractCommand::shouldDisplay)
                .filter(cmd-> cmd.getName().equalsIgnoreCase(args.get(0)))
                .collect(Collectors.toList());
        if (sub.size() == 1) {
            args.remove(0);
            return sub.get(0).get(args);
        }
        return this;
    }

    public List<? extends AbstractCommand> getChildren() {
        return this.children;
    }
}
