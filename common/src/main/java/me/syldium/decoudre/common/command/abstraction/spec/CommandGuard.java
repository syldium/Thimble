package me.syldium.decoudre.common.command.abstraction.spec;

import me.syldium.decoudre.common.DeCoudrePlugin;
import me.syldium.decoudre.common.command.abstraction.CommandException;
import me.syldium.decoudre.common.command.abstraction.Sender;
import me.syldium.decoudre.common.player.Message;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

/**
 * Defines restrictions for executing a command.
 */
@FunctionalInterface
public interface CommandGuard {

    /**
     * Executes the guard.
     *
     * <p>Throwing an exception will prevent execution and display a message to the user.</p>
     *
     * @param plugin The plugin.
     * @param sender The command sender.
     * @throws CommandException If the state of the sender does not allow the execution.
     */
    void check(@NotNull DeCoudrePlugin plugin, @NotNull Sender sender) throws CommandException;

    /**
     * Tests if a predicate is {@code true} to execute the command.
     *
     * @param predicate A predicate.
     * @param orElseExceptionMessage Or else message.
     * @return A new command guard.
     */
    static @NotNull CommandGuard except(@NotNull BiPredicate<DeCoudrePlugin, Sender> predicate, Message orElseExceptionMessage) {
        return (plugin, sender) -> {
            if (!predicate.test(plugin, sender)) {
                throw new CommandException(orElseExceptionMessage);
            }
        };
    }


    /**
     * Ensures that the player is not in a game.
     */
    CommandGuard EXCEPT_NOT_IN_GAME = CommandGuard.except(
        (plugin, sender) -> !(plugin.getGamesService().getGame(sender.getUuid()).isPresent()),
        Message.IN_GAME
    );

    /**
     * Verifies that the player is in a game.
     */
    CommandGuard EXCEPT_IN_GAME = CommandGuard.except(
        (plugin, sender) -> plugin.getGamesService().getGame(sender.getUuid()).isPresent(),
        Message.NOT_IN_GAME
    );
}
