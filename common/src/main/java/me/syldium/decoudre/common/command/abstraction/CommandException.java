package me.syldium.decoudre.common.command.abstraction;

import me.syldium.decoudre.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

public class CommandException extends RuntimeException {

    private final MessageKey messageKey;

    public CommandException(@NotNull MessageKey messageKey) {
        this.messageKey = messageKey;
    }

    public @NotNull MessageKey getMessageKey() {
        return this.messageKey;
    }

    public static class ArgumentParseException extends CommandException {

        public ArgumentParseException(@NotNull MessageKey messageKey) {
            super(messageKey);
        }
    }
}
