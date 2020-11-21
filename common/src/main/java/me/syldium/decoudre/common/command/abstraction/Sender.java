package me.syldium.decoudre.common.command.abstraction;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Sender extends Audience {

    UUID CONSOLE_UUID = new UUID(0, 0);
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

    /**
     * Checks whether this sender has the permission.
     *
     * @param permission The permission to check.
     * @return Whether the sender has the permission given.
     */
    boolean hasPermission(@NotNull String permission);
}
