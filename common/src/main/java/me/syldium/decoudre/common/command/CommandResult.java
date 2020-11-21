package me.syldium.decoudre.common.command;

import me.syldium.decoudre.common.player.MessageKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandResult {

    private final boolean isSuccess;
    private final MessageKey messageKey;

    CommandResult(boolean isSuccess, @Nullable MessageKey messageKey, @NotNull String... placeholders) {
        this.isSuccess = isSuccess;
        this.messageKey = messageKey;
    }

    public static @NotNull CommandResult success(@NotNull String... placeholders) {
        return new CommandResult(true, null, placeholders);
    }

    public static @NotNull CommandResult success(@NotNull MessageKey messageKey, @NotNull String... placeholders) {
        return new CommandResult(true, messageKey, placeholders);
    }

    public static @NotNull CommandResult error(@NotNull String... placeholders) {
        return new CommandResult(false, null, placeholders);
    }

    public static @NotNull CommandResult error(@NotNull MessageKey messageKey, @NotNull String... placeholders) {
        return new CommandResult(false, messageKey, placeholders);
    }

    public @Nullable MessageKey getMessageKey() {
        return this.messageKey;
    }

    public @NotNull TextColor getTextColor() {
        return this.isSuccess ? NamedTextColor.GREEN : NamedTextColor.RED;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }
}
