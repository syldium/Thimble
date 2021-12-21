package me.syldium.thimble.common.command.abstraction;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.command.CommandResult;
import me.syldium.thimble.common.player.MessageKey;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Sender extends Audience {

    UUID CONSOLE_UUID = Identity.nil().uuid();
    String CONSOLE_NAME = "Console";

    /**
     * The UUID of this sender. {@link #CONSOLE_UUID}'s UUID if it is the console.
     *
     * @return The UUID of the sender.
     */
    @NotNull UUID uuid();

    /**
     * The name of this sender. {@link #CONSOLE_NAME}'s name if it is the console.
     *
     * @return The name of the sender.
     */
    @NotNull String name();

    default @NotNull Component displayName() {
        return Component.text(this.name());
    }

    /**
     * Checks whether this sender has the permission.
     *
     * @param permission The permission to check.
     * @return Whether the sender has the permission given.
     */
    boolean hasPermission(@NotNull String permission);

    /**
     * Sends a command feedback.
     *
     * @param feedback A command result.
     */
    void sendFeedback(@NotNull CommandResult feedback);

    /**
     * Sends a chat message from its {@link MessageKey}.
     *
     * @param key The message key.
     * @param placeholders Some placeholders.
     */
    void sendMessage(@NotNull MessageKey key, Placeholder<?>... placeholders);

    /**
     * Sends a message to the player's action bar from its {@link MessageKey}.
     *
     * @param key The message key.
     * @param placeholders Some placeholders.
     */
    void sendActionBar(@NotNull MessageKey key, Placeholder<?>... placeholders);

    /**
     * Gets the plugin instance.
     *
     * @return The instance.
     */
    @NotNull ThimblePlugin getPlugin();
}
