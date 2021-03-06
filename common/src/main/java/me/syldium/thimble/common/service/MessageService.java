package me.syldium.thimble.common.service;

import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface MessageService {

    String MESSAGES_BUNDLE = "messages";

    /**
     * Gets the plugin prefix.
     *
     * @return The prefix.
     */
    @NotNull Component prefix();

    /**
     * Load the language files into memory.
     */
    void updateLocale();

    /**
     * Gets the translated string from a {@link MessageKey}, and formats it.
     *
     * @param key The message key.
     * @param templates Some placeholders.
     * @return The formatted message component.
     */
    default @NotNull Component formatMessage(@NotNull MessageKey key, @NotNull Template... templates) {
        return this.formatMessage(key, null, templates);
    }

    /**
     * Gets the translated string from a {@link MessageKey}, and formats it.
     *
     * @param key The message key.
     * @param templates Some placeholders.
     * @return The formatted message component.
     */
    default @NotNull Component formatMessageWithPrefix(@NotNull MessageKey key, @NotNull Template... templates) {
        return this.prefix().append(this.formatMessage(key, templates));
    }

    default @NotNull Component formatMessage(@NotNull CommandResult feedback) {
        Objects.requireNonNull(feedback.getMessageKey(), "Message key");
        return this.formatMessage(feedback.getMessageKey(), feedback.getTextColor(), feedback.getTemplates());
    }

    /**
     * Gets the translated string from a {@link MessageKey}, and formats it.
     *
     * @param key The message key.
     * @param color A default color.
     * @param templates Some placeholders.
     * @return The formatted message component.
     */
    @NotNull Component formatMessage(@NotNull MessageKey key, @Nullable TextColor color, @NotNull Template... templates);

    @NotNull String get(@NotNull MessageKey key);
}
