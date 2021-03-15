package me.syldium.thimble.common.service;

import me.syldium.thimble.api.player.ThimblePlayer;
import me.syldium.thimble.common.player.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PlaceholderService {

    PlaceholderService EMPTY = (player, text) -> text;

    /**
     * Translates all external placeholders into their corresponding values.
     *
     * @param player The player to parse the placeholders against.
     * @param text The string to set the placeholder values in.
     * @return A string containing all translated placeholders.
     */
    @NotNull String setPlaceholders(@NotNull Player player, @NotNull String text);

    @FunctionalInterface
    interface Thimble {

        /**
         * Translates all external placeholders into their corresponding values.
         *
         * @param player The player to parse the placeholders against.
         * @param text The string to set the placeholder values in.
         * @return A string containing all translated placeholders.
         */
        @NotNull String setPlaceholders(@NotNull ThimblePlayer player, @NotNull String text);
    }
}
