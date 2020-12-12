package me.syldium.thimble.common.command.abstraction;

import me.syldium.thimble.common.player.MessageKey;
import org.jetbrains.annotations.NotNull;

public final class Permission {

    public static @NotNull Permission arenaSetup(@NotNull String property) {
        return new Permission("decoudre.arena." + property);
    }

    public static @NotNull Permission arenaSetup() {
        return new Permission("decoudre.arena");
    }

    public static @NotNull Permission player(@NotNull String property) {
        return new Permission("decoudre.player." + property);
    }

    public static @NotNull Permission stats(@NotNull String property) {
        return new Permission("decoudre.stats." + property);
    }

    public static @NotNull Permission stats() {
        return new Permission("decoudre.stats");
    }

    private final String permission;

    Permission(@NotNull String permission) {
        this.permission = permission;
    }

    public void verify(@NotNull Sender sender) {
        if (!sender.hasPermission(this.permission)) {
            throw new CommandException(MessageKey.FEEDBACK_UNKNOWN_COMMAND);
        }
    }

    public @NotNull String getPermission() {
        return this.permission;
    }
}
