package me.syldium.decoudre.common.command.abstraction;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.CommandResult;
import me.syldium.decoudre.common.command.abstraction.spec.Argument;
import me.syldium.decoudre.common.command.abstraction.spec.CommandGuard;
import me.syldium.decoudre.common.game.Game;
import me.syldium.decoudre.common.player.Message;
import me.syldium.decoudre.common.player.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ChildCommand extends AbstractCommand {

    private final List<Argument<?>> arguments;
    private final int minArgumentCount;
    private final Component help;
    private final Component usage;
    protected @Nullable CommandGuard commandGuard;

    public ChildCommand(@NotNull String name, @Nullable Message description, @NotNull Permission permission, Argument<?> ...arguments) {
        super(name, description, permission);
        this.arguments = Arrays.asList(arguments);
        this.minArgumentCount = (int) Arrays.stream(arguments).filter(Argument::isRequired).count();
        Component args = Component.join(Component.text(name + " "), this.arguments);
        this.help = description == null ? args : args.append(Component.text(" ").append(description.asComponent()));
        this.usage = description == null ? args : args.hoverEvent(HoverEvent.showText(description.asComponent()));
    }

    @Override
    public void preExecute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender) throws CommandException {
        if (this.commandGuard != null) {
            this.commandGuard.check(plugin, sender);
        }
    }

    @Override
    public @NotNull List<@NotNull String> tabComplete(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) {
        if (args.size() < 1 || !sender.hasPermission(this.permission.getPermission())) {
            return Collections.emptyList();
        }

        if (args.size() <= this.arguments.size()) {
            return this.arguments.get(args.size() - 1).tabComplete(plugin, args.get(args.size() - 1), sender);
        }
        return Collections.emptyList();
    }

    @Override
    public @NotNull Component getHelp() {
        return this.help;
    }

    @Override
    public @NotNull Component getUsage() {
        return this.usage;
    }

    @Override
    public int getMinArgumentCount() {
        return this.minArgumentCount;
    }

    @Override
    public @NotNull AbstractCommand get(@NotNull List<@NotNull String> args) {
        return this;
    }

    protected @NotNull Game getGame(@NotNull DeCoudrePlugin plugin, @NotNull Player player) throws CommandException {
        return (Game) plugin.getGamesService().getGame(player.getUuid()).orElseThrow(() -> new CommandException(Message.NOT_IN_GAME));
    }

    public List<Argument<?>> getArguments() {
        return this.arguments;
    }

    public abstract static class One<T> extends ChildCommand {

        private final Argument<T> one;

        public One(@NotNull String name, @NotNull Argument<T> argument, @Nullable Message description, @NotNull Permission permission) {
            super(name, description, permission, argument);
            this.one = argument;
        }

        public abstract @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, T arg) throws CommandException;

        @Override
        public final @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
            return this.execute(plugin, sender, this.one.parse(plugin, args.get(0)));
        }
    }

    public abstract static class Two<T, U> extends ChildCommand {

        private final Argument<T> one;
        private final Argument<U> two;

        public Two(@NotNull String name, @NotNull Argument<T> argument, @NotNull Argument<U> argument2, @Nullable Message description, @NotNull Permission permission) {
            super(name, description, permission, argument, argument2);
            this.one = argument;
            this.two = argument2;
        }

        public abstract @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, T arg, U arg2) throws CommandException;

        @Override
        public final @NotNull CommandResult execute(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender, @NotNull List<String> args) throws CommandException {
            return this.execute(plugin, sender, this.one.parse(plugin, args.get(0)), this.two.parse(plugin, args.get(1)));
        }
    }
}
