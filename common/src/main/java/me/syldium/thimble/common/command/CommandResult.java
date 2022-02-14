package me.syldium.thimble.common.command;

import me.syldium.thimble.common.player.MessageKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandResult {

    private final int type;
    private final MessageKey messageKey;
    private final TagResolver placeholders;

    CommandResult(boolean isSuccess, @Nullable MessageKey messageKey, @NotNull TagResolver placeholders) {
        this(isSuccess ? 1 : 0, messageKey, placeholders);
    }

    CommandResult(int type, @Nullable MessageKey messageKey, @NotNull TagResolver placeholders) {
        this.type = type;
        this.messageKey = messageKey;
        this.placeholders = placeholders;
    }

    public static @NotNull CommandResult info(@NotNull MessageKey messageKey, @NotNull TagResolver placeholders) {
        return new CommandResult(2, messageKey, placeholders);
    }

    public static @NotNull CommandResult success() {
        return new CommandResult(true, null, TagResolver.empty());
    }

    public static @NotNull CommandResult success(@NotNull TagResolver placeholders) {
        return new CommandResult(true, null, placeholders);
    }

    public static @NotNull CommandResult success(@NotNull MessageKey messageKey) {
        return new CommandResult(true, messageKey, TagResolver.empty());
    }

    public static @NotNull CommandResult success(@NotNull MessageKey messageKey, @NotNull TagResolver placeholders) {
        return new CommandResult(true, messageKey, placeholders);
    }

    public static @NotNull CommandResult success(@NotNull MessageKey messageKey, @NotNull TagResolver... placeholders) {
        return new CommandResult(true, messageKey, TagResolver.resolver(placeholders));
    }

    public static @NotNull CommandResult error() {
        return new CommandResult(false, null, TagResolver.empty());
    }

    public static @NotNull CommandResult error(@NotNull TagResolver placeholders) {
        return new CommandResult(false, null, placeholders);
    }

    public static @NotNull CommandResult error(@NotNull MessageKey messageKey) {
        return new CommandResult(false, messageKey, TagResolver.empty());
    }

    public static @NotNull CommandResult error(@NotNull MessageKey messageKey, @NotNull TagResolver placeholders) {
        return new CommandResult(false, messageKey, placeholders);
    }

    public static @NotNull CommandResult error(@NotNull MessageKey messageKey, @NotNull TagResolver... placeholders) {
        return new CommandResult(false, messageKey, TagResolver.resolver(placeholders));
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

    public @NotNull TagResolver getPlaceholders() {
        return this.placeholders;
    }

    public boolean isInfo() {
        return this.type == 2;
    }

    public boolean isSuccess() {
        return this.type == 1;
    }
}
