package me.syldium.thimble.common.command.abstraction;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.command.abstraction.spec.Argument;
import me.syldium.thimble.common.command.abstraction.spec.CommandGuard;
import me.syldium.thimble.common.game.Game;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.service.MessageService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ChildCommand extends AbstractCommand {

    private final List<Argument<?>> arguments;
    private final int minArgumentCount;
    private final Component component;
    protected @Nullable CommandGuard commandGuard;

    public ChildCommand(@NotNull String name, @Nullable MessageKey description, @NotNull Permission permission, Argument<?> ...arguments) {
        super(name, description, permission);
        this.arguments = Arrays.asList(arguments);
        this.minArgumentCount = (int) Arrays.stream(arguments).filter(Argument::isRequired).count();
        this.component = this.buildComponent(name, arguments);
    }

    @Override
    public void preExecute(@NotNull ThimblePlugin plugin, @NotNull Sender sender) throws CommandException {
        if (this.commandGuard != null) {
            this.commandGuard.check(plugin, sender);
        }
    }

    @Override
    public @NotNull List<@NotNull String> tabComplete(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) {
        if (args.size() < 1 || !sender.hasPermission(this.permission.get())) {
            return Collections.emptyList();
        }

        if (args.size() <= this.arguments.size()) {
            return this.arguments.get(args.size() - 1).tabComplete(plugin, args.get(args.size() - 1), sender);
        }
        return Collections.emptyList();
    }

    @Override
    public @NotNull Component getHelp(@NotNull MessageService service) {
        if (this.description == null) {
            return this.component;
        }
        return this.component.append(Component.space()).append(service.formatMessage(this.description, NamedTextColor.GRAY));
    }

    @Override
    public @NotNull Component getUsage(@NotNull MessageService service) {
        if (this.description == null) {
            return this.component;
        }
        return this.component.hoverEvent(HoverEvent.showText(service.formatMessage(this.description)));
    }

    @Override
    public int getMinArgumentCount() {
        return this.minArgumentCount;
    }

    @Override
    public @NotNull AbstractCommand get(@NotNull List<@NotNull String> args) {
        return this;
    }

    @Override @SuppressWarnings("unchecked")
    public <T extends AbstractCommand> @Nullable T lookup(@NotNull Class<T> clazz) {
        if (this.getClass().equals(clazz)) {
            return (T) this;
        }
        return null;
    }

    protected @NotNull Game getGame(@NotNull ThimblePlugin plugin, @NotNull Sender player) throws CommandException {
        return (Game) plugin.getGameService().playerGame(player.uuid()).orElseThrow(() -> new CommandException(MessageKey.FEEDBACK_GAME_NOT_IN_GAME));
    }

    private @NotNull Component buildComponent(@NotNull String name, @NotNull Argument<?>[] arguments) {
        if (arguments.length < 1) {
            return Component.text(name);
        }
        return Component.text(name).append(Component.space().append(Component.join(Component.text(" "), this.arguments)));
    }

    public List<Argument<?>> getArguments() {
        return this.arguments;
    }

    public abstract static class One<T> extends ChildCommand {

        private final Argument<T> one;

        public One(@NotNull String name, @NotNull Argument<T> argument, @Nullable MessageKey description, @NotNull Permission permission) {
            super(name, description, permission, argument);
            this.one = argument;
        }

        public abstract @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, T arg) throws CommandException;

        @Override
        public final @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
            return this.execute(plugin, sender, args.size() > 0 ? this.one.parse(plugin, args.get(0)) : null);
        }
    }

    public abstract static class Two<T, U> extends ChildCommand {

        private final Argument<T> one;
        private final Argument<U> two;

        public Two(@NotNull String name, @NotNull Argument<T> argument, @NotNull Argument<U> argument2, @Nullable MessageKey description, @NotNull Permission permission) {
            super(name, description, permission, argument, argument2);
            this.one = argument;
            this.two = argument2;
        }

        public abstract @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, T arg, U arg2) throws CommandException;

        @Override
        public final @NotNull CommandResult execute(@NotNull ThimblePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
            T one = args.size() > 0 ? this.one.parse(plugin, args.get(0)) : null;
            U two = args.size() > 1 ? this.two.parse(plugin, args.get(1)) : null;
            return this.execute(plugin, sender, one, two);
        }
    }
}
