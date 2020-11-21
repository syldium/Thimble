package me.syldium.decoudre.common.service;

import me.syldium.decoudre.common.player.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessageService {

    /**
     * Gets the translated string from a {@link MessageKey}, and formats it.
     *
     * @param key The message key.
     * @param templates Some placeholders.
     * @return The formatted message component.
     */
    @NotNull Component formatMessage(@NotNull MessageKey key, @NotNull Template... templates);

    /**
     * Gets the translated string from a {@link MessageKey}, and formats it.
     *
     * @param key The message key.
     * @param color A default color.
     * @return The formatted message component.
     */
    @NotNull Component formatMessage(@NotNull MessageKey key, @Nullable TextColor color, @NotNull String... placeholders);
}
