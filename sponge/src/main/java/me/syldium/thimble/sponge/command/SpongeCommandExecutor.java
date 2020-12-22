package me.syldium.thimble.sponge.command;

import me.syldium.thimble.common.command.CommandManager;
import me.syldium.thimble.common.command.abstraction.Sender;
import me.syldium.thimble.sponge.ThSpongePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.spongeapi.SpongeComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public class SpongeCommandExecutor extends CommandManager implements CommandCallable {

    private final ThSpongePlugin plugin;

    public SpongeCommandExecutor(@NotNull ThSpongePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSource source, @NotNull String arguments) throws CommandException {
        Sender sender = this.plugin.getPlayerAdapter().asAbstractSender(source);
        me.syldium.thimble.common.command.CommandResult result = this.executeCommand(this.plugin, sender, "dac", arguments);
        if (result.isSuccess()) {
            sender.sendFeedback(result);
            return CommandResult.success();
        }

        if (result.getMessageKey() != null) {
            Component component = this.plugin.getMessageService().prefix().append(this.plugin.getMessageService().formatMessage(result));
            throw new CommandException(SpongeComponentSerializer.get().serialize(component));
        }
        return CommandResult.empty();
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull CommandSource source, @NotNull String arguments, @Nullable Location<World> targetPosition) {
        return this.tabCompleteCommand(this.plugin, this.plugin.getPlayerAdapter().asAbstractSender(source), arguments);
    }

    @Override
    public boolean testPermission(@NotNull CommandSource source) {
        return true;
    }

    @Override
    public @NotNull Optional<Text> getShortDescription(@NotNull CommandSource source) {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<Text> getHelp(@NotNull CommandSource source) {
        return Optional.empty();
    }

    @Override
    public @NotNull Text getUsage(@NotNull CommandSource source) {
        return null;
    }
}
