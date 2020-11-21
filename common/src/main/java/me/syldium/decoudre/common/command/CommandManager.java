package me.syldium.decoudre.common.command;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.AbstractCommand;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.command.arena.CreateCommand;
import me.syldium.decoudre.common.command.arena.SetJumpCommand;
import me.syldium.decoudre.common.command.arena.SetSpawnCommand;
import me.syldium.decoudre.common.command.game.JoinCommand;
import me.syldium.decoudre.common.command.game.LeaveCommand;
import me.syldium.decoudre.common.player.MessageKey;
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
                new CreateCommand(),
                new SetSpawnCommand(),
                new SetJumpCommand()
        );
    }

    private @NotNull CommandResult executeCommand(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull String label, @NotNull List<@NotNull String> arguments) {
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
            result = main.execute(plugin, sender, arguments);
        } catch (CommandException ex) {
            result = CommandResult.error(ex.getMessageKey());
        }

        return result;
    }

    public @NotNull CommandResult runCommand(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull String label, @NotNull List<@NotNull String> arguments) {
        CommandResult result = this.executeCommand(plugin, sender, label, arguments);
        sender.sendFeedback(result);
        return result;
    }

    public @NotNull List<@NotNull String> tabCompleteCommand(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<@NotNull String> arguments) {
        if (arguments.size() == 1) {
            return this.mainCommands.stream()
                    .filter(AbstractCommand::shouldDisplay)
                    .filter(c -> c.getName().startsWith(arguments.get(0)))
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

    private void sendMainHelp(@NotNull Sender sender, @NotNull String label) {
        for (AbstractCommand cmd : this.mainCommands) {
            if (!cmd.hasPermission(sender) || !cmd.shouldDisplay()) {
                continue;
            }
            sender.sendMessage(Component.text("/" + label + " ", NamedTextColor.GREEN).append(cmd.getHelp(sender.getPlugin().getMessageService())));
        }
    }

    protected @NotNull List<@NotNull ? extends AbstractCommand> getMainCommands() {
        return this.mainCommands;
    }
}
