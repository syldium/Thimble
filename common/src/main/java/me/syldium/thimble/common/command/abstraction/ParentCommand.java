package me.syldium.thimble.common.command.abstraction;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.syldium.thimble.common.command.CommandManager.sendHelp;

public class ParentCommand extends AbstractCommand {

    private final List<AbstractCommand> children;
    private final Component usage;

    public ParentCommand(String name, List<AbstractCommand> children, MessageKey description, Permission permission) {
        super(name, description, permission);
        this.children = children;
        this.usage = Component.text(name);
        children.forEach(c -> c.parent = this);
    }

    @Override
    public @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
        if (args.size() > 0 && !args.get(0).equalsIgnoreCase("help")) {
            throw new CommandException(MessageKey.FEEDBACK_UNKNOWN_COMMAND);
        }

        sendHelp(sender, "th " + this.getPath(), this.children);
        return CommandResult.success();
    }

    @Override
    public @NotNull List<@NotNull String> tabComplete(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) {
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
    public @NotNull Component getHelp(@NotNull MessageService service) {
        return this.usage;
    }

    public @NotNull Component getUsage(@NotNull MessageService service) {
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

    @Override @SuppressWarnings("unchecked")
    public <T extends AbstractCommand> @Nullable T lookup(@NotNull Class<T> clazz) {
        if (this.getClass().equals(clazz)) {
            return (T) this;
        }
        for (AbstractCommand command : this.children) {
            T c = command.lookup(clazz);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public @NotNull List<@NotNull AbstractCommand> getChildren() {
        return this.children;
    }

    @Override
    public boolean shouldDisplay() {
        return !this.children.isEmpty();
    }
}
