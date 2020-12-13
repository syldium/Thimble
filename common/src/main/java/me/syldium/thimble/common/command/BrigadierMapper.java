package me.syldium.thimble.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.syldium.thimble.common.command.abstraction.AbstractCommand;
import me.syldium.thimble.common.command.abstraction.ChildCommand;
import me.syldium.thimble.common.command.abstraction.ParentCommand;
import me.syldium.thimble.common.command.abstraction.spec.Argument;
import me.syldium.thimble.common.command.abstraction.spec.BrigadierArgumentMapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class BrigadierMapper<S> {

    private final CommandManager commandManager;
    private final BiPredicate<AbstractCommand, S> permissionPredicate;

    public BrigadierMapper(@NotNull CommandManager commandManager, @NotNull BiPredicate<AbstractCommand, S> permissionPredicate) {
        this.commandManager = commandManager;
        this.permissionPredicate = permissionPredicate;
    }

    public @NotNull LiteralCommandNode<S> build(@NotNull LiteralCommandNode<S> node, @NotNull SuggestionProvider<S> suggestionProvider) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(node.getLiteral());
        builder.then(LiteralArgumentBuilder.literal("help"));

        for (AbstractCommand command : this.commandManager.getMainCommands()) {
            if (command.shouldDisplay()) {
                builder.then(this.buildCommandNode(command, suggestionProvider));
            }
        }
        return builder.build();
    }

    protected @NotNull CommandNode<S> buildCommandNode(@NotNull AbstractCommand command, @NotNull SuggestionProvider<S> suggestionProvider) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.<S>literal(command.getName())
                .requires(sender -> this.permissionPredicate.test(command, sender));
        if (command instanceof ParentCommand) {
            builder.then(LiteralArgumentBuilder.literal("help"));
            for (AbstractCommand subCommand : ((ParentCommand) command).getChildren()) {
                if (subCommand.shouldDisplay()) {
                    builder.then(this.buildCommandNode(subCommand, suggestionProvider));
                }
            }
            return builder.build();
        }

        ChildCommand cmd = (ChildCommand) command;
        CommandNode<S> node = builder.build();
        CommandNode<S> prevNode = node;
        for (int i = 0; i < cmd.getArguments().size(); i++) {
            RequiredArgumentBuilder<S, ?> arg = this.getFromArg(cmd.getArguments().get(i));
            if (!(arg.getType() instanceof IntegerArgumentType)) {
                arg.suggests(suggestionProvider);
            }
            if (i < cmd.getArguments().size() - 1 && !cmd.getArguments().get(i + 1).isRequired()) {
                arg.executes(s -> 0);
            }
            prevNode.addChild(prevNode = arg.build());
        }
        return node;
    }

    protected @NotNull RequiredArgumentBuilder<S, ?> getFromArg(@NotNull Argument<?> arg) {
        return RequiredArgumentBuilder.argument(arg.getName(), BrigadierArgumentMapper.getArgumentType(arg));
    }

    /**
     * Returns a literal node that redirects its execution to the given destination node.
     *
     * <p><a href="https://github.com/Mojang/brigadier/issues/46#issuecomment-661849914">Issue</a></p>
     *
     * @param alias The command alias.
     * @param destination The destination node.
     * @return The built node.
     */
    public @NotNull LiteralCommandNode<S> buildRedirect(@NotNull String alias, @NotNull LiteralCommandNode<S> destination) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.<S>literal(alias)
                .requires(destination.getRequirement())
                .forward(destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());
        for (CommandNode<S> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }
}
