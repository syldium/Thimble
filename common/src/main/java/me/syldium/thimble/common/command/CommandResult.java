package me.syldium.thimble.common.command;

import me.syldium.thimble.common.player.MessageKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandResult {

    private final int type;
    private final MessageKey messageKey;
    private final Placeholder<?>[] placeholders;

    CommandResult(boolean isSuccess, @Nullable MessageKey messageKey, @NotNull Placeholder<?>... placeholders) {
        this(isSuccess ? 1 : 0, messageKey, placeholders);
    }

    CommandResult(int type, @Nullable MessageKey messageKey, @NotNull Placeholder<?>... placeholders) {
        this.type = type;
        this.messageKey = messageKey;
        this.placeholders = placeholders;
    }

    public static @NotNull CommandResult info(@NotNull MessageKey messageKey, @NotNull Placeholder<?>... placeholders) {
        return new CommandResult(2, messageKey, placeholders);
    }

    public static @NotNull CommandResult success(@NotNull Placeholder<?>... placeholders) {
        return new CommandResult(true, null, placeholders);
    }

    public static @NotNull CommandResult success(@NotNull MessageKey messageKey, @NotNull Placeholder<?>... placeholders) {
        return new CommandResult(true, messageKey, placeholders);
    }

    public static @NotNull CommandResult error(@NotNull Placeholder<?>... placeholders) {
        return new CommandResult(false, null, placeholders);
    }

    public static @NotNull CommandResult error(@NotNull MessageKey messageKey, @NotNull Placeholder<?>... placeholders) {
        return new CommandResult(false, messageKey, placeholders);
    }

    public @Nullable MessageKey getMessageKey() {
        return this.messageKey;
    }

    public @NotNull TextColor getTextColor() {
        if (this.type > 1) {
            return NamedTextColor.GRAY;
        }
        return this.isSuccess() ? NamedTextColor.GREEN : NamedTextColor.RED;
    }

    public @NotNull Placeholder<?>[] getPlaceholders() {
        return this.placeholders;
    }

    public boolean isInfo() {
        return this.type == 2;
    }

    public boolean isSuccess() {
        return this.type == 1;
    }
}
