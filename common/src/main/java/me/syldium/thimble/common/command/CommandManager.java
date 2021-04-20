package me.syldium.thimble.common.command;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.abstraction.AbstractCommand;
import me.syldium.thimble.common.command.abstraction.CommandException;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.common.command.arena.ArenaCommand;
import me.syldium.thimble.common.command.game.BlockCommand;
import me.syldium.thimble.common.command.game.JoinCommand;
import me.syldium.thimble.common.command.game.LeaveCommand;
import me.syldium.thimble.common.command.game.StatsCommand;
import me.syldium.thimble.common.command.migrate.MigrateCommand;
import me.syldium.thimble.common.command.migrate.ReloadCommand;
import me.syldium.thimble.common.command.migrate.VersionCommand;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager {

    private final List<? extends AbstractCommand> mainCommands;

    public CommandManager() {
        this.mainCommands = Arrays.asList(
                new JoinCommand(),
                new LeaveCommand(),
                new BlockCommand(),
                new ArenaCommand(),
                new StatsCommand(),
                new MigrateCommand(),
                new ReloadCommand(),
                new VersionCommand()
        );
    }

    /**
     * Executes the command without showing any {@link CommandResult}.
     *
     * @param plugin The plugin instance.
     * @param sender The command sender.
     * @param label The command label.
     * @param arguments The command arguments.
     * @return Not handled command result.
     */
    protected @NotNull CommandResult executeCommand(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull String label, @NotNull List<@NotNull String> arguments) {
        if (arguments.size() < 1 || arguments.get(0).equalsIgnoreCase("help")) {
            this.sendMainHelp(sender, label);
            return CommandResult.success();
        }

        String token = arguments.get(0);
        AbstractCommand main = this.mainCommands.stream()
                .filter(cmd -> cmd.getName().equals(token))
                .findFirst()
                .orElse(null);

        if (main == null) {
            return CommandResult.error(MessageKey.FEEDBACK_UNKNOWN_COMMAND);
        }

        arguments.remove(0);
        main = main.get(arguments);

        if (!main.hasPermission(sender)) {
            return CommandResult.error(MessageKey.FEEDBACK_UNKNOWN_COMMAND);
        }

        if (!main.isValidExecutor(sender)) {
            return CommandResult.error(MessageKey.FEEDBACK_NOT_VALID_EXECUTOR);
        }

        if (main.getMinArgumentCount() > arguments.size()) {
            sender.sendMessage(Component.text("Usage: /" + label + " ", NamedTextColor.RED).append(main.getUsage(plugin.getMessageService())));
            return CommandResult.error();
        }

        try {
            main.preExecute(plugin, sender);
        } catch (CommandException ex) {
            return CommandResult.error(ex.getMessageKey());
        }

        CommandResult result;
        try {
            result = main.execute(plugin, sender, arguments, label);
        } catch (CommandException ex) {
            result = CommandResult.error(ex.getMessageKey());
        }

        return result;
    }

    /**
     * Executes the command without showing any {@link CommandResult}.
     *
     * @param plugin The plugin instance.
     * @param sender The command sender.
     * @param label The command label.
     * @param arguments The command arguments.
     * @return Not handled command result.
     */
    protected @NotNull CommandResult executeCommand(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull String label, @NotNull String arguments) {
        return this.executeCommand(plugin, sender, label, this.getArgumentsList(arguments));
    }

    /**
     * Executes the command and displays the feedback.
     *
     * @param plugin The plugin instance.
     * @param sender The command sender.
     * @param label The command label.
     * @param arguments The command arguments.
     * @return Result of the command execution.
     */
    protected @NotNull CommandResult runCommand(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull String label, @NotNull List<@NotNull String> arguments) {
        CommandResult result = this.executeCommand(plugin, sender, label, arguments);
        sender.sendFeedback(result);
        return result;
    }

    /**
     * Executes the command and displays the feedback.
     *
     * @param plugin The plugin instance.
     * @param sender The command sender.
     * @param label The command label.
     * @param arguments The command arguments.
     * @return Result of the command execution.
     */
    protected @NotNull CommandResult runCommand(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull String label, @NotNull String arguments) {
        return this.runCommand(plugin, sender, label, this.getArgumentsList(arguments));
    }

    protected @NotNull List<@NotNull String> tabCompleteCommand(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<@NotNull String> arguments) {
        if (arguments.size() < 2) {
            String arg = arguments.isEmpty() ? "" : arguments.get(0);
            return this.mainCommands.stream()
                    .filter(AbstractCommand::shouldDisplay)
                    .filter(c -> c.getName().startsWith(arg))
                    .filter(c -> c.hasPermission(sender))
                    .map(AbstractCommand::getName)
                    .collect(Collectors.toList());
        }

        Optional<? extends AbstractCommand> main = this.mainCommands.stream()
                .filter(AbstractCommand::shouldDisplay)
                .filter(cmd -> cmd.getName().equals(arguments.get(0)))
                .findFirst();

        if (main.isPresent()) {
            arguments.remove(0);
            return main.get().get(arguments).tabComplete(plugin, sender, arguments);
        }

        return Collections.emptyList();
    }

    protected @NotNull List<@NotNull String> tabCompleteCommand(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull String arguments) {
        return this.tabCompleteCommand(plugin, sender, this.getArgumentsList(arguments));
    }

    private void sendMainHelp(@NotNull Sender sender, @NotNull String label) {
        sendHelp(sender, label, this.mainCommands);
    }

    public static void sendHelp(@NotNull Sender sender, @NotNull String label, @NotNull List<@NotNull ? extends AbstractCommand> commands) {
        for (AbstractCommand cmd : commands) {
            if (!cmd.hasPermission(sender) || !cmd.shouldDisplay()) {
                continue;
            }
            sender.sendMessage(Component.text("/" + label + " ", NamedTextColor.GREEN).append(cmd.getHelp(sender.getPlugin().getMessageService())));
        }
    }

    protected @NotNull List<@NotNull ? extends AbstractCommand> getMainCommands() {
        return this.mainCommands;
    }

    public @NotNull List<@NotNull String> getArgumentsList(@NotNull String arguments) {
        return StringUtil.split(arguments, ' ');
    }

    public <T extends AbstractCommand> @NotNull T lookup(@NotNull Class<T> clazz) {
        for (AbstractCommand cmd : this.mainCommands) {
            T command = cmd.lookup(clazz);
            if (command != null) {
                return command;
            }
        }
        throw new IllegalArgumentException("This command is not registered!");
    }
}
