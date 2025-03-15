package me.syldium.thimble.sponge.command;

import me.syldium.thimble.common.command.CommandManager;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.sponge.ThSpongePlugin;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.ArgumentReader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class SpongeCommandExecutor extends CommandManager implements Command.Raw {

    protected final ThSpongePlugin plugin;

    public SpongeCommandExecutor(@NotNull ThSpongePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) {
        Sender sender = this.plugin.getPlayerAdapter().asAbstractSender(cause);
        me.syldium.thimble.common.command.CommandResult feedback = executeCommand(this.plugin, sender, "th", arguments.input());
        if (feedback.isError()) {
            return CommandResult.error(this.plugin.getMessageService().formatMessage(feedback));
        }
        sender.sendFeedback(feedback);
        return CommandResult.success();
    }

    @Override
    public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) {
        Sender sender = this.plugin.getPlayerAdapter().asAbstractSender(cause);
        return tabCompleteCommand(this.plugin, sender, arguments.input())
                .stream()
                .map(CommandCompletion::of)
                .collect(Collectors.toList());
    }

    @Override
    public boolean canExecute(CommandCause cause) {
        return true;
    }

    @Override
    public Optional<Component> shortDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Component usage(CommandCause cause) {
        return text("/th");
    }
}
