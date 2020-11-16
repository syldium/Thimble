package me.syldium.decoudre.common.command.abstraction;

import me.syldium.decoudre.common.player.Message;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class CommandException extends RuntimeException {

    private final Component message;

    public CommandException(@NotNull Component message) {
        this.message = message;
    }

    public CommandException(@NotNull Message message) {
        this(message.format());
    }

    public void send(@NotNull Sender sender) {
        sender.sendMessage(this.message);
    }

    public static class ArgumentParseException extends CommandException {
        public ArgumentParseException(@NotNull Component message) {
            super(message);
        }

        public ArgumentParseException(@NotNull Message message) {
            super(message);
        }
    }
}
